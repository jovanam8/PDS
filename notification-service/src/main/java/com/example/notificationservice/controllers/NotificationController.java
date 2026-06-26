package com.example.notificationservice.controllers;

import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.dto.NotificationResponseDTO;
import com.example.notificationservice.models.Notification;
import com.example.notificationservice.services.NotificationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {

    private final NotificationService service;
    private final ModelMapper mapper;

    public NotificationController(NotificationService service, ModelMapper mapper) { this.service = service; this.mapper = mapper; }

    @GetMapping
    public List<NotificationResponseDTO> list() { return service.findAll().stream().map(n -> mapper.map(n, NotificationResponseDTO.class)).collect(Collectors.toList()); }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> get(@PathVariable Long id) {
        Notification n = service.findById(id);
        if (n == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(n, NotificationResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@Valid @RequestBody NotificationRequestDTO notificationDto) {
        Notification notification = mapper.map(notificationDto, Notification.class);
        Notification created = service.create(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(created, NotificationResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody NotificationRequestDTO notificationDto) {
        Notification notification = mapper.map(notificationDto, Notification.class);
        Notification updated = service.update(id, notification);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(updated, NotificationResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

