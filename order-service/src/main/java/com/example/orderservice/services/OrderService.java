package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.OrderDetailsDTO;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.exceptions.NotFoundException;
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
                .orElseThrow(()-> new NotFoundException("Order with id " + id + " not found"));
    }

    public OrderResponseDTO create(OrderRequestDTO orderDto) {
        UserDTO user = proxyService.getUserProtected(orderDto.getUserId());

        if(user == null ) throw new NotFoundException("User with id " + orderDto.getUserId() + " not found");

        ProductDTO product = proxyService.getProductProtected(orderDto.getProductId());
        if(product == null) throw  new NotFoundException("Product with id " + orderDto.getProductId() + " not found");

        proxyService.reduceStock(orderDto.getProductId(), orderDto.getQuantity());

        Order order = mapper.map(orderDto, Order.class);
        return toResponseDto(repository.save(order));
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO orderDto) {
        Order existing = repository.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));

        ProductDTO product = proxyService.getProductProtected(orderDto.getProductId());
        if(product == null) throw new NotFoundException("Product with id " + orderDto.getProductId() + " not found");

        UserDTO user = proxyService.getUserProtected(orderDto.getUserId());
        if(user == null) throw new NotFoundException("User with id " + orderDto.getUserId() + " not found");

        if (existing.getProductId().equals(orderDto.getProductId())) {
            int diff = orderDto.getQuantity() - existing.getQuantity();
            if (diff > 0) {
                proxyService.reduceStock(orderDto.getProductId(), diff);
            } else if (diff < 0) {
                proxyService.addStock(orderDto.getProductId(), -diff); // treba dodati u OrderProxyService, analogno reduceStock
            }
        } else {
            proxyService.addStock(existing.getProductId(), existing.getQuantity());
            proxyService.reduceStock(orderDto.getProductId(), orderDto.getQuantity());
        }

        existing.setProductId(orderDto.getProductId());
        existing.setUserId(orderDto.getUserId());
        existing.setQuantity(orderDto.getQuantity());
        return toResponseDto(repository.save(existing));
    }

    public void delete(Long id) { repository.deleteById(id); }

    public OrderDetailsDTO getOrderDetails(Long orderId) {
        Order order = repository.findById(orderId).orElse(null);
        if (order == null) throw new NotFoundException("Order with id " + orderId + " not found");

        UserDTO user = proxyService.getUserProtected(order.getUserId());
        if (user == null) throw new NotFoundException("User with id" + order.getUserId() + " not found");

        ProductDTO product = proxyService.getProductProtected(order.getProductId());
        if (product == null) throw new NotFoundException("Product with id" + order.getProductId() + " not found");

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

