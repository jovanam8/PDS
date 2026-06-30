package com.example.orderservice.clients;

import com.example.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    @PostMapping("/api/products/reduce-stock/{id}")
    void reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PostMapping("/api/products/add-stock/{id}")
    void addStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}
