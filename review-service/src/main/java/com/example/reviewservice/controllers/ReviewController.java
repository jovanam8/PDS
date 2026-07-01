package com.example.reviewservice.controllers;

import com.example.reviewservice.dto.ReviewDetailsDTO;
import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ReviewDetailsDTO> getReviewDetails(@PathVariable Long id) {
        return ResponseEntity.ok(service.getReviewDetails(id));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@Valid @RequestBody ReviewRequestDTO reviewDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(reviewDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ReviewRequestDTO reviewDto) {
        return ResponseEntity.ok(service.update(id, reviewDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.findByProductId(productId));
    }
}

