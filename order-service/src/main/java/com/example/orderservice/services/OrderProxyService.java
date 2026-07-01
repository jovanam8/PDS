package com.example.orderservice.services;

import com.example.orderservice.clients.ProductClient;
import com.example.orderservice.clients.UserClient;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.exceptions.InsufficientStockException;
import com.example.orderservice.exceptions.ServiceUnavailableException;
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
        throw new ServiceUnavailableException("User service is currently unavailable");
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
        throw new ServiceUnavailableException("Product service unavailable");
        //return new ProductDTO(productId, "Unavailable Product", "Product details unavailable", 0.0d);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "reduceStockFallback")
    @Retry(name = "productService")
    public void reduceStock(Long id, Integer quantity){
        try {
            productClient.reduceStock(id, quantity);
        } catch (FeignException.Conflict e) {
            throw new InsufficientStockException("Not enough stock for product " + id);
        }
    }

    public void reduceStockFallback(Long id, Integer quantity, Throwable throwable){
        if (throwable instanceof InsufficientStockException) throw (InsufficientStockException) throwable;
        throw new ServiceUnavailableException("Product service is currently unavailable for reduce stock");
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "addStockFallback")
    @Retry(name = "productService")
    public void addStock(Long id, Integer quantity){
        productClient.addStock(id, quantity);
    }

    public void addStockFallback(Long id, Integer quantity, Throwable throwable){
        throw new ServiceUnavailableException("Product service is currently unavailable for add stock");
    }
}

