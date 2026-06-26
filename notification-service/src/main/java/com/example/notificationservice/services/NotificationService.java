package com.example.notificationservice.services;

import com.example.notificationservice.models.Notification;
import com.example.notificationservice.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) { this.repository = repository; }

    public List<Notification> findAll() { return repository.findAll(); }
    public Notification findById(Long id) { return repository.findById(id).orElse(null); }
    public Notification create(Notification n) { return repository.save(n); }
    public Notification update(Long id, Notification n) {
        return repository.findById(id).map(existing -> {
            existing.setUserId(n.getUserId());
            existing.setMessage(n.getMessage());
            return repository.save(existing);
        }).orElse(null);
    }
    public void delete(Long id) { repository.deleteById(id); }
}

