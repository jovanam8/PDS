package com.example.notificationservice.clients;

import com.example.notificationservice.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderClient {
    @GetMapping("/api/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") Long orderId);
}

