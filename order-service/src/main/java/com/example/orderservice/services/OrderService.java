package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.OrderDetailsDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderService(OrderRepository repository, UserClient userClient, ProductClient productClient) {
        this.repository = repository;
        this.userClient = userClient;
        this.productClient = productClient;
    }

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

    public OrderDetailsDTO getOrderDetails(Long orderId) {
        Order order = repository.findById(orderId).orElse(null);
        if (order == null) return  null; //exception kasnije

        //feign
        UserDTO user = userClient.getUserById(order.getUserId());
        ProductDTO product = productClient.getProductById(order.getProductId());

        OrderDetailsDTO response = new OrderDetailsDTO();
        response.setId(order.getId());
        response.setQuantity(order.getQuantity());
        response.setUser(user);
        response.setProduct(product);

        return response;

    }
}

