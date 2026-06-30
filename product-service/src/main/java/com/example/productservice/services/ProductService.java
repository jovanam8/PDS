package com.example.productservice.services;

import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductResponseDTO;
import com.example.productservice.dto.ProductDetailsDTO;
import com.example.productservice.dto.ReviewDTO;
import com.example.productservice.models.Product;
import com.example.productservice.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductProxyService productProxyService;
    private final ModelMapper mapper;

    public ProductService(ProductRepository repository, ProductProxyService productProxyService, ModelMapper mapper) {
        this.repository = repository;
        this.productProxyService = productProxyService;
        this.mapper = mapper;
    }

    public List<ProductResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public ProductResponseDTO create(ProductRequestDTO productDto) {
        Product product = mapper.map(productDto, Product.class);
        return toResponseDto(repository.save(product));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO productDto) {
        Product p = mapper.map(productDto, Product.class);
        return repository.findById(id).map(existing -> {
            existing.setName(p.getName());
            existing.setDescription(p.getDescription());
            existing.setPrice(p.getPrice());
            existing.setStock(p.getStock());
            return toResponseDto(repository.save(existing));
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }

    public ProductDetailsDTO getProductDetails(Long id) {
        Product product = repository.findById(id).orElse(null);
        if (product == null) return null;

        List<ReviewDTO> reviews = productProxyService.getReviewsByProductId(id);

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
        response.setStock(product.getStock());
        response.setReviews(reviews);
        response.setAverageRating(averageRating);
        response.setTotalReviews(reviews.size());

        return response;
    }

    public void reduceStock(Long id, Integer quantity){
        Product product = repository.findById(id).orElse(null);
        if(product.getStock() < quantity)
            throw new RuntimeException("Not enough stock");

        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }

    public void addStock(Long id, Integer quantity) {
        Product product = repository.findById(id).orElseThrow();

        product.setStock(product.getStock() + quantity);
        repository.save(product);
    }

    private ProductResponseDTO toResponseDto(Product product) {
        return mapper.map(product, ProductResponseDTO.class);
    }
}

