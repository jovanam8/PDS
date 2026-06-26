package com.example.reviewservice.services;

import com.example.reviewservice.models.Review;
import com.example.reviewservice.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) { this.repository = repository; }

    public List<Review> findAll() { return repository.findAll(); }
    public Review findById(Long id) { return repository.findById(id).orElse(null); }
    public Review create(Review r) { return repository.save(r); }
    public Review update(Long id, Review r) {
        return repository.findById(id).map(existing -> {
            existing.setProductId(r.getProductId());
            existing.setUserId(r.getUserId());
            existing.setRating(r.getRating());
            existing.setComment(r.getComment());
            return repository.save(existing);
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }
}

