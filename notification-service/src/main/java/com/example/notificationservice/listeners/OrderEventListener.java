package com.example.notificationservice.listeners;

import com.example.notificationservice.config.RabbitConfig;
import com.example.notificationservice.dto.NotificationRequestDTO;
import com.example.notificationservice.events.OrderCreatedEvent;
import com.example.notificationservice.models.ProcessedEvent;
import com.example.notificationservice.repositories.ProcessedEventRepository;
import com.example.notificationservice.services.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    public OrderEventListener(NotificationService notificationService, ProcessedEventRepository processedEventRepository) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
    }

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        if (processedEventRepository.existsByEventId(event.getEventId())) {
            return; // već obrađeno — idempotentnost, preskoči duplikat
        }

        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(event.getUserId());
        dto.setMessage(event.getMessage());
        notificationService.create(dto);

        processedEventRepository.save(new ProcessedEvent(event.getEventId()));
    }
}