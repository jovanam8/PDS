package com.example.notificationservice.controllers;

import com.example.notificationservice.dto.NotificationDetailsDTO;
import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.dto.NotificationResponseDTO;
import com.example.notificationservice.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> get(@PathVariable Long id) {
        NotificationResponseDTO notification = service.findById(id);
        if (notification == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<NotificationDetailsDTO> getNotificationDetails(@PathVariable Long id) {
        NotificationDetailsDTO details = service.getNotificationDetails(id);
        if (details == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(details);
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@Valid @RequestBody NotificationRequestDTO notificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(notificationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody NotificationRequestDTO notificationDto) {
        NotificationResponseDTO updated = service.update(id, notificationDto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

