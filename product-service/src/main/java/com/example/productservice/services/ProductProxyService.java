package com.example.productservice.services;

import com.example.productservice.clients.ReviewClient;
import com.example.productservice.dto.ReviewDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductProxyService {

    private final ReviewClient reviewClient;

    public ProductProxyService(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    @CircuitBreaker(name = "reviewService", fallbackMethod = "getReviewsByProductIdFallback")
    @Retry(name = "reviewService")
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        return reviewClient.getReviewsByProductId(productId);
    }

    public List<ReviewDTO> getReviewsByProductIdFallback(Long productId, Throwable throwable) {
        return List.of();
    }
}

