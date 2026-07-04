package com.example.orderservice.services;

import com.example.orderservice.config.RabbitConfig;
import com.example.orderservice.dto.OrderDetailsDTO;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.events.OrderCreatedEvent;
import com.example.orderservice.exceptions.NotFoundException;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final ExternalClientService externalClientService;
    private final ModelMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository repository, ExternalClientService externalClientService, ModelMapper mapper, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.externalClientService = externalClientService;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
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
        UserDTO user = externalClientService.getUserProtected(orderDto.getUserId());

        if(user == null ) throw new NotFoundException("User with id " + orderDto.getUserId() + " not found");

        ProductDTO product = externalClientService.getProductProtected(orderDto.getProductId());
        if(product == null) throw  new NotFoundException("Product with id " + orderDto.getProductId() + " not found");

        externalClientService.reduceStock(orderDto.getProductId(), orderDto.getQuantity());

        Order order = mapper.map(orderDto, Order.class);
        Order saved = repository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getUserId(),
                "Order #" + saved.getId() + " successfully created for user " + saved.getUserId()
        );

        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE, RabbitConfig.ORDER_CREATED_ROUTING_KEY, event);
        return toResponseDto(saved);
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO orderDto) {
        Order existing = repository.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));

        ProductDTO product = externalClientService.getProductProtected(orderDto.getProductId());
        if(product == null) throw new NotFoundException("Product with id " + orderDto.getProductId() + " not found");

        UserDTO user = externalClientService.getUserProtected(orderDto.getUserId());
        if(user == null) throw new NotFoundException("User with id " + orderDto.getUserId() + " not found");

        if (existing.getProductId().equals(orderDto.getProductId())) {
            int diff = orderDto.getQuantity() - existing.getQuantity();
            if (diff > 0) {
                externalClientService.reduceStock(orderDto.getProductId(), diff);
            } else if (diff < 0) {
                externalClientService.addStock(orderDto.getProductId(), -diff);
            }
        } else {
            externalClientService.addStock(existing.getProductId(), existing.getQuantity());
            externalClientService.reduceStock(orderDto.getProductId(), orderDto.getQuantity());
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

        UserDTO user = externalClientService.getUserProtected(order.getUserId());
        if (user == null) throw new NotFoundException("User with id" + order.getUserId() + " not found");

        ProductDTO product = externalClientService.getProductProtected(order.getProductId());
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

