package com.example.reviewservice.controllers;

import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.models.Review;
import com.example.reviewservice.services.ReviewService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {

    private final ReviewService service;
    private final ModelMapper mapper;

    public ReviewController(ReviewService service, ModelMapper mapper) { this.service = service; this.mapper = mapper; }

    @GetMapping
    public List<ReviewResponseDTO> list() { return service.findAll().stream().map(r -> mapper.map(r, ReviewResponseDTO.class)).collect(Collectors.toList()); }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> get(@PathVariable Long id) {
        Review r = service.findById(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(r, ReviewResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@Valid @RequestBody ReviewRequestDTO reviewDto) {
        Review review = mapper.map(reviewDto, Review.class);
        Review created = service.create(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(created, ReviewResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ReviewRequestDTO reviewDto) {
        Review review = mapper.map(reviewDto, Review.class);
        Review updated = service.update(id, review);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(updated, ReviewResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    public List<ReviewResponseDTO> getByProduct(@PathVariable Long productId) {
        return service.findByProductId(productId)
                .stream()
                .map(r -> mapper.map(r, ReviewResponseDTO.class))
                .collect(Collectors.toList());
    }
}

