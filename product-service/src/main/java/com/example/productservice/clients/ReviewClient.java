package com.example.productservice.clients;

import com.example.productservice.dto.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewClient {
    @GetMapping("/api/reviews/product/{productId}")
    List<ReviewDTO> getReviewsByProductId(@PathVariable("productId") Long productId);
}
