package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import feign.FeignException;
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
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    public UserDTO getUserFallback(Long userId, Throwable throwable) {
        throw new RuntimeException("User service unavailable");
        //return new UserDTO(userId, "Unavailable User", "unavailable@example.com");
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    public ProductDTO getProductProtected(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            return null; // proizvod legitimno ne postoji, nije problem servisa
        }
    }

    public ProductDTO getProductFallback(Long productId, Throwable throwable) {
        throw new RuntimeException("Product service unavailable");
        //return new ProductDTO(productId, "Unavailable Product", "Product details unavailable", 0.0d);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "reduceStockFallback")
    @Retry(name = "productService")
    public void reduceStock(Long id, Integer quantity){
        productClient.reduceStock(id,quantity);
    }

    public void reduceStockFallback(Long id, Integer quantity, Throwable throwable){
        throw new RuntimeException("Product service unavailable for reduce stock");
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "addStockFallback")
    @Retry(name = "productService")
    public void addStock(Long id, Integer quantity){
        productClient.addStock(id, quantity);
    }

    public void addStockFallback(Long id, Integer quantity, Throwable throwable){
        throw new RuntimeException("Product service unavailable for add stock");
    }
}

