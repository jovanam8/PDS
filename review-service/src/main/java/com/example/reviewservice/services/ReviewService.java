package com.example.reviewservice.services;

import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.models.Review;
import com.example.reviewservice.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final ModelMapper mapper;

    public ReviewService(ReviewRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ReviewResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public ReviewResponseDTO create(ReviewRequestDTO reviewDto) {
        Review review = mapper.map(reviewDto, Review.class);
        return toResponseDto(repository.save(review));
    }

    public ReviewResponseDTO update(Long id, ReviewRequestDTO reviewDto) {
        Review r = mapper.map(reviewDto, Review.class);
        return repository.findById(id).map(existing -> {
            existing.setProductId(r.getProductId());
            existing.setUserId(r.getUserId());
            existing.setRating(r.getRating());
            existing.setComment(r.getComment());
            return toResponseDto(repository.save(existing));
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }
    public List<ReviewResponseDTO> findByProductId(Long productId){
        return repository.findByProductId(productId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO toResponseDto(Review review) {
        return mapper.map(review, ReviewResponseDTO.class);
    }
}

