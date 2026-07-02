package com.example.productservice.services;

import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductResponseDTO;
import com.example.productservice.dto.ProductDetailsDTO;
import com.example.productservice.dto.ReviewDTO;
import com.example.productservice.exceptions.InsufficientStockException;
import com.example.productservice.exceptions.NotFoundException;
import com.example.productservice.models.Product;
import com.example.productservice.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ExternalClientService externalClientService;
    private final ModelMapper mapper;

    public ProductService(ProductRepository repository, ExternalClientService externalClientService, ModelMapper mapper) {
        this.repository = repository;
        this.externalClientService = externalClientService;
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
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
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
        }).orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
    }
    public void delete(Long id) { repository.deleteById(id); }

    public ProductDetailsDTO getProductDetails(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));

        List<ReviewDTO> reviews = externalClientService.getReviewsByProductId(id);

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
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
        if(product.getStock() < quantity)
            throw new InsufficientStockException("Not enough stock for product with id " + id);

        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }

    public void addStock(Long id, Integer quantity) {
        Product product = repository.findById(id).orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));

        product.setStock(product.getStock() + quantity);
        repository.save(product);
    }

    private ProductResponseDTO toResponseDto(Product product) {
        return mapper.map(product, ProductResponseDTO.class);
    }
}

