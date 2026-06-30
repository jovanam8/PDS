package com.example.notificationservice.services;

import com.example.notificationservice.clients.OrderClient;
import com.example.notificationservice.clients.UserClient;
import com.example.notificationservice.dto.OrderDTO;
import com.example.notificationservice.dto.UserDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class NotificationProxyService {

    private final UserClient userClient;
    private final OrderClient orderClient;

    public NotificationProxyService(UserClient userClient, OrderClient orderClient) {
        this.userClient = userClient;
        this.orderClient = orderClient;
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
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    public OrderDTO getOrderProtected(Long orderId) {
        try {
            return orderClient.getOrderById(orderId);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    public OrderDTO getOrderFallback(Long orderId, Throwable throwable) {
        throw new RuntimeException("Order service unavailable");
    }
}

