package com.example.productservice.services;

import com.example.productservice.clients.ReviewClient;
import com.example.productservice.dto.ProductDetailsDTO;
import com.example.productservice.dto.ReviewDTO;
import com.example.productservice.models.Product;
import com.example.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ReviewClient reviewClient;

    public ProductService(ProductRepository repository, ReviewClient reviewClient) {
        this.repository = repository;
        this.reviewClient = reviewClient;
    }

    public List<Product> findAll() { return repository.findAll(); }
    public Product findById(Long id) { return repository.findById(id).orElse(null); }
    public Product create(Product p) { return repository.save(p); }
    public Product update(Long id, Product p) {
        return repository.findById(id).map(existing -> {
            existing.setName(p.getName());
            existing.setDescription(p.getDescription());
            existing.setPrice(p.getPrice());
            return repository.save(existing);
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }

    public ProductDetailsDTO getProductDetails(Long id) {
        Product product = repository.findById(id).orElse(null);
        if (product == null) return null;

        // Feign poziv ka review-service
        List<ReviewDTO> reviews = reviewClient.getReviewsByProductId(id);

        double averageRating = reviews.stream()
                .mapToInt(ReviewDTO::getRating)
                .average()
                .orElse(0.0);
        averageRating = Math.round(averageRating * 100.0) / 100.0;

        ProductDetailsDTO response = new ProductDetailsDTO();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setReviews(reviews);
        response.setAverageRating(averageRating);
        response.setTotalReviews(reviews.size());

        return response;
    }
}

