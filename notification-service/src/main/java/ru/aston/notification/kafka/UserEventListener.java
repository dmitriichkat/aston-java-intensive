package ru.aston.notification.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.service.EmailService;

@Component
public class UserEventListener {
    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    private final EmailService emailService;

    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-events}")
    public void onUserEvent(UserEvent event) {
        logger.info("Received {} event for user {}", event.type(), event.id());
        emailService.send(event.email(), event.type());
    }
}
