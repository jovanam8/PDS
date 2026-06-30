package com.example.reviewservice.services;

import com.example.reviewservice.dto.ReviewRequestDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.dto.ProductDTO;
import com.example.reviewservice.dto.ReviewDetailsDTO;
import com.example.reviewservice.dto.UserDTO;
import com.example.reviewservice.models.Review;
import com.example.reviewservice.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final ReviewProxyService proxyService;
    private final ModelMapper mapper;

    public ReviewService(ReviewRepository repository, ReviewProxyService proxyService, ModelMapper mapper) {
        this.repository = repository;
        this.proxyService = proxyService;
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
        UserDTO user = proxyService.getUserProtected(reviewDto.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        ProductDTO product = proxyService.getProductProtected(reviewDto.getProductId());
        if (product == null) throw new RuntimeException("Product not found");

        Review review = mapper.map(reviewDto, Review.class);
        return toResponseDto(repository.save(review));
    }

    public ReviewResponseDTO update(Long id, ReviewRequestDTO reviewDto) {
        Review r = mapper.map(reviewDto, Review.class);

        ProductDTO product = proxyService.getProductProtected(reviewDto.getProductId());
        if (product == null) throw new RuntimeException("Product not found");

        UserDTO user = proxyService.getUserProtected(reviewDto.getUserId());
        if (user == null) throw new RuntimeException("User not found");

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

    public ReviewDetailsDTO getReviewDetails(Long reviewId) {
        Review review = repository.findById(reviewId).orElse(null);
        if (review == null) return null;

        UserDTO user = proxyService.getUserProtected(review.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        ProductDTO product = proxyService.getProductProtected(review.getProductId());
        if (product == null) throw new RuntimeException("Product not found");

        ReviewDetailsDTO response = new ReviewDetailsDTO();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setUserId(review.getUserId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setUser(user);
        response.setProduct(product);

        return response;
    }

    private ReviewResponseDTO toResponseDto(Review review) {
        return mapper.map(review, ReviewResponseDTO.class);
    }
}

