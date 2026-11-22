package ru.aston.notification.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.notification.dto.UserEvent;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    private final String from;
    private final String subject;
    private final String siteName;

    public EmailService(JavaMailSender javaMailSender, @Value("${app.mail.from}") String from,
                        @Value("${app.mail.subject}") String subject, @Value("${app.site-name}") String siteName) {
        this.javaMailSender = javaMailSender;
        this.from = from;
        this.subject = subject;
        this.siteName = siteName;
    }

    @CircuitBreaker(name = "emailCircuitBreaker", fallbackMethod = "sendFallback")
    public void send(String email, UserEvent.UserEventType type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(buildText(type));
        javaMailSender.send(message);
        logger.info("Email about {} sent", type);
    }

    private void sendFallback(String email, UserEvent.UserEventType type, Throwable t) {
        logger.error("CircuitBreaker 'emailCircuitBreaker' is OPEN. Failed to send email about {} to {}. Cause: {}",
                type, email, t.getMessage());
    }

    public String buildText(UserEvent.UserEventType type) {
        return switch (type) {
            case CREATED -> "Здравствуйте! Ваш аккаунт на сайте " + siteName + " был успешно создан.";
            case DELETED -> "Здравствуйте! Ваш аккаунт был удалён.";
        };
    }
}