package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationDetailsDTO {
    private Long id;
    private Long userId;
    private Long orderId;
    private String message;
    private UserDTO user;
    private OrderDTO order;
}

