package com.example.productservice.controllers;

import com.example.productservice.dto.ProductDetailsDTO;
import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductResponseDTO;
import com.example.productservice.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> list() {
       return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(productDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productDto) {
        return ResponseEntity.ok(service.update(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProductDetailsDTO> getProductDetails(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductDetails(id));
    }

    @PostMapping("/reduce-stock/{id}")
    public void reduceStock(@PathVariable Long id, @RequestParam Integer quantity){
        service.reduceStock(id,quantity);
    }

    @PostMapping("/add-stock/{id}")
    public void addStock(@PathVariable Long id, @RequestParam Integer quantity){
        service.addStock(id,quantity);
    }

}
