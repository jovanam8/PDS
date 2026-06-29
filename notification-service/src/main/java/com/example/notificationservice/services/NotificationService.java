package com.example.notificationservice.services;

import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.dto.NotificationResponseDTO;
import com.example.notificationservice.models.Notification;
import com.example.notificationservice.repositories.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final ModelMapper mapper;

    public NotificationService(NotificationRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<NotificationResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public NotificationResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public NotificationResponseDTO create(NotificationRequestDTO notificationDto) {
        Notification notification = mapper.map(notificationDto, Notification.class);
        return toResponseDto(repository.save(notification));
    }

    public NotificationResponseDTO update(Long id, NotificationRequestDTO notificationDto) {
        Notification n = mapper.map(notificationDto, Notification.class);
        return repository.findById(id).map(existing -> {
            existing.setUserId(n.getUserId());
            existing.setMessage(n.getMessage());
            return toResponseDto(repository.save(existing));
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }

    private NotificationResponseDTO toResponseDto(Notification notification) {
        return mapper.map(notification, NotificationResponseDTO.class);
    }
}

