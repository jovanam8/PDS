package com.example.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewResponseDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;
}

