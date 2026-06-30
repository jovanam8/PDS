package com.example.notificationservice.services;

import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.dto.NotificationResponseDTO;
import com.example.notificationservice.dto.NotificationDetailsDTO;
import com.example.notificationservice.dto.OrderDTO;
import com.example.notificationservice.dto.UserDTO;
import com.example.notificationservice.models.Notification;
import com.example.notificationservice.repositories.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationProxyService proxyService;
    private final ModelMapper mapper;

    public NotificationService(NotificationRepository repository, NotificationProxyService proxyService, ModelMapper mapper) {
        this.repository = repository;
        this.proxyService = proxyService;
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
        UserDTO user = proxyService.getUserProtected(notificationDto.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        if (notificationDto.getOrderId() != null) {
            OrderDTO order = proxyService.getOrderProtected(notificationDto.getOrderId());
            if (order == null) throw new RuntimeException("Order not found");
        }

        Notification notification = mapper.map(notificationDto, Notification.class);
        return toResponseDto(repository.save(notification));
    }

    public NotificationResponseDTO update(Long id, NotificationRequestDTO notificationDto) {
        Notification n = mapper.map(notificationDto, Notification.class);

        UserDTO user = proxyService.getUserProtected(notificationDto.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        if (notificationDto.getOrderId() != null) {
            OrderDTO order = proxyService.getOrderProtected(notificationDto.getOrderId());
            if (order == null) throw new RuntimeException("Order not found");
        }

        return repository.findById(id).map(existing -> {
            existing.setUserId(n.getUserId());
            existing.setOrderId(n.getOrderId());
            existing.setMessage(n.getMessage());
            return toResponseDto(repository.save(existing));
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }

    public NotificationDetailsDTO getNotificationDetails(Long notificationId) {
        Notification notification = repository.findById(notificationId).orElse(null);
        if (notification == null) return null;

        UserDTO user = proxyService.getUserProtected(notification.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        OrderDTO order = null;
        if (notification.getOrderId() != null) {
            order = proxyService.getOrderProtected(notification.getOrderId());
            if (order == null) throw new RuntimeException("Order not found");
        }

        NotificationDetailsDTO response = new NotificationDetailsDTO();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setOrderId(notification.getOrderId());
        response.setMessage(notification.getMessage());
        response.setUser(user);
        response.setOrder(order);

        return response;
    }

    private NotificationResponseDTO toResponseDto(Notification notification) {
        return mapper.map(notification, NotificationResponseDTO.class);
    }
}

