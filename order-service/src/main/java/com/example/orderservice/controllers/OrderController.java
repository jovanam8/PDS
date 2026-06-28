package com.example.orderservice.controllers;

import com.example.orderservice.dto.OrderDetailsDTO;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.models.Order;
import com.example.orderservice.services.OrderService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService service;
    private final ModelMapper mapper;

    public OrderController(OrderService service, ModelMapper mapper) { this.service = service; this.mapper = mapper; }

    @GetMapping
    public List<OrderResponseDTO> list() { return service.findAll().stream().map(o -> mapper.map(o, OrderResponseDTO.class)).collect(Collectors.toList()); }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> get(@PathVariable Long id) {
        Order o = service.findById(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(o, OrderResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO orderDto) {
        Order order = mapper.map(orderDto, Order.class);
        Order created = service.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(created, OrderResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(@PathVariable Long id, @Valid @RequestBody OrderRequestDTO orderDto) {
        Order order = mapper.map(orderDto, Order.class);
        Order updated = service.update(id, order);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(updated, OrderResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/details")
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Long id){
        OrderDetailsDTO details = service.getOrderDetails(id);
        if(details == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(details);
    }
}

