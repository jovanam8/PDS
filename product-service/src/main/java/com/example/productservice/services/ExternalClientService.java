package com.example.productservice.services;

import com.example.productservice.clients.ReviewClient;
import com.example.productservice.dto.ReviewDTO;
import com.example.productservice.exceptions.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;
//sloj enkapsulira sve pozive ka drugim mikroservisima, zaštićene Resilience4j-em
@Service
public class ExternalClientService {

    private final ReviewClient reviewClient;

    public ExternalClientService(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    @CircuitBreaker(name = "reviewService", fallbackMethod = "getReviewsByProductIdFallback")
    @Retry(name = "reviewService")
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<ReviewDTO> reviews = reviewClient.getReviewsByProductId(productId);
        if(reviews == null) return List.of();

        return reviews;
    }

    public List<ReviewDTO> getReviewsByProductIdFallback(Long productId, Throwable throwable) {
        throw new ServiceUnavailableException("Review service is currently unavailable");
    }
}

