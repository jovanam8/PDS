package com.example.reviewservice.services;

import com.example.reviewservice.clients.ProductClient;
import com.example.reviewservice.clients.UserClient;
import com.example.reviewservice.dto.ProductDTO;
import com.example.reviewservice.dto.UserDTO;
import com.example.reviewservice.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class ExternalClientService {

    private final UserClient userClient;
    private final ProductClient productClient;

    public ExternalClientService(UserClient userClient, ProductClient productClient) {
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
        throw new ServiceUnavailableException("User service unavailable");
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    public ProductDTO getProductProtected(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    public ProductDTO getProductFallback(Long productId, Throwable throwable) {
        throw new ServiceUnavailableException("Product service unavailable");
    }
}

