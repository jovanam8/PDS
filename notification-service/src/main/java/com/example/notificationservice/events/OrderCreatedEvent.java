package com.example.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent implements Serializable {
    private UUID eventId;
    private Long orderId;
    private Long userId;
    private String message;
}
