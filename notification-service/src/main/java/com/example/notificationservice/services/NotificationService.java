package com.example.notificationservice.services;

import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.dto.NotificationResponseDTO;
import com.example.notificationservice.dto.NotificationDetailsDTO;
import com.example.notificationservice.dto.UserDTO;
import com.example.notificationservice.exceptions.NotFoundException;
import com.example.notificationservice.models.Notification;
import com.example.notificationservice.repositories.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final ExternalClientService externalClientService;
    private final ModelMapper mapper;

    public NotificationService(NotificationRepository repository, ExternalClientService externalClientService, ModelMapper mapper) {
        this.repository = repository;
        this.externalClientService = externalClientService;
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
                .orElseThrow(() -> new NotFoundException("Notification with id " + id + " not found"));
    }

    public NotificationResponseDTO create(NotificationRequestDTO notificationDto) {
        UserDTO user = externalClientService.getUserProtected(notificationDto.getUserId());
        if (user == null) throw new NotFoundException("User with id " + notificationDto.getUserId() + " not found");

        Notification notification = mapper.map(notificationDto, Notification.class);
        return toResponseDto(repository.save(notification));
    }

    public NotificationResponseDTO update(Long id, NotificationRequestDTO notificationDto) {
        Notification n = mapper.map(notificationDto, Notification.class);

        UserDTO user = externalClientService.getUserProtected(notificationDto.getUserId());
        if (user == null) throw new NotFoundException("User with id " + notificationDto.getUserId() + " not found");

        return repository.findById(id).map(existing -> {
            existing.setUserId(n.getUserId());
            existing.setMessage(n.getMessage());
            return toResponseDto(repository.save(existing));
        }).orElseThrow(() -> new NotFoundException("Notification with id " + id + " not found"));
    }
    public void delete(Long id) { repository.deleteById(id); }

    public NotificationDetailsDTO getNotificationDetails(Long notificationId) {
        Notification notification = repository.findById(notificationId).orElseThrow(() -> new NotFoundException("Notification with id " + notificationId + " not found"));

        UserDTO user = externalClientService.getUserProtected(notification.getUserId());
        if (user == null) throw new NotFoundException("User with id " + notification.getUserId() + " not found");

        NotificationDetailsDTO response = new NotificationDetailsDTO();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setUser(user);

        return response;
    }

    private NotificationResponseDTO toResponseDto(Notification notification) {
        return mapper.map(notification, NotificationResponseDTO.class);
    }
}

