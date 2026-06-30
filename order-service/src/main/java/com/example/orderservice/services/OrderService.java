package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.OrderDetailsDTO;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderProxyService proxyService;
    private final ModelMapper mapper;

    public OrderService(OrderRepository repository, OrderProxyService proxyService, ModelMapper mapper) {
        this.repository = repository;
        this.proxyService = proxyService;
        this.mapper = mapper;
    }

    public List<OrderResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public OrderResponseDTO create(OrderRequestDTO orderDto) {
        UserDTO user = proxyService.getUserProtected(orderDto.getUserId());
        //nije null circuit breaker vrati instancu promeniti uslov ili ono sto vraca
        if(user == null ) throw new RuntimeException("User not found"); //dodati posle konkretnu klasu exceptiona

        ProductDTO product = proxyService.getProductProtected(orderDto.getProductId());
        if(product == null) throw  new RuntimeException("Product not found");

        Order order = mapper.map(orderDto, Order.class);

        proxyService.reduceStock(product.getId(), order.getQuantity());
        return toResponseDto(repository.save(order));
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO orderDto) {
        Order o = mapper.map(orderDto, Order.class);

        ProductDTO product = proxyService.getProductProtected(orderDto.getProductId());
        if(product == null) throw new RuntimeException("Product not found");

        UserDTO user = proxyService.getUserProtected(orderDto.getUserId());
        if(user == null) throw  new RuntimeException("User not found");

        return repository.findById(id).map(existing -> {
            existing.setProductId(o.getProductId());
            existing.setUserId(o.getUserId());
            existing.setQuantity(o.getQuantity());
            return toResponseDto(repository.save(existing));
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }

    public OrderDetailsDTO getOrderDetails(Long orderId) {
        Order order = repository.findById(orderId).orElse(null);
        if (order == null) return  null; //exception kasnije

        UserDTO user = proxyService.getUserProtected(order.getUserId());
        ProductDTO product = proxyService.getProductProtected(order.getProductId());

        OrderDetailsDTO response = new OrderDetailsDTO();
        response.setId(order.getId());
        response.setQuantity(order.getQuantity());
        response.setUser(user);
        response.setProduct(product);

        return response;
    }

    private OrderResponseDTO toResponseDto(Order order) {
        return mapper.map(order, OrderResponseDTO.class);
    }
}

