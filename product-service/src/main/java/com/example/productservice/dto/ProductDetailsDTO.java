package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<ReviewDTO> reviews;
    private Double averageRating;
    private Integer totalReviews;
}
