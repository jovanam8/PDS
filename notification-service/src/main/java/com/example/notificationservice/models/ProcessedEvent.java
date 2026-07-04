package com.example.notificationservice.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
public class ProcessedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true, nullable = false)
    private UUID eventId;

    private Instant processedAt = Instant.now();

    public ProcessedEvent() {}
    public ProcessedEvent(UUID eventId) { this.eventId = eventId; }

}
