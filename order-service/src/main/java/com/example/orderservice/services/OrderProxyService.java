package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class OrderProxyService {

    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderProxyService(UserClient userClient, ProductClient productClient) {
        this.userClient = userClient;
        this.productClient = productClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    public UserDTO getUserProtected(Long userId) {
        return userClient.getUserById(userId);
    }

    public UserDTO getUserFallback(Long userId, Throwable throwable) {
        return new UserDTO(userId, "Unavailable User", "unavailable@example.com");
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    public ProductDTO getProductProtected(Long productId) {
        return productClient.getProductById(productId);
    }

    public ProductDTO getProductFallback(Long productId, Throwable throwable) {
        return new ProductDTO(productId, "Unavailable Product", "Product details unavailable", 0.0d);
    }
}

