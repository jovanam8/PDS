package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetailsDTO {
    private Long id;
    private Integer quantity;
    private UserDTO user;
    private ProductDTO product;
}
