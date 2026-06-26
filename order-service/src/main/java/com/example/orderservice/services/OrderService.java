package com.example.orderservice.services;

import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) { this.repository = repository; }

    public List<Order> findAll() { return repository.findAll(); }
    public Order findById(Long id) { return repository.findById(id).orElse(null); }
    public Order create(Order o) { return repository.save(o); }
    public Order update(Long id, Order o) {
        return repository.findById(id).map(existing -> {
            existing.setProductId(o.getProductId());
            existing.setUserId(o.getUserId());
            existing.setQuantity(o.getQuantity());
            return repository.save(existing);
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }
}

