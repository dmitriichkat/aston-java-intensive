package ru.aston.notification.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.notification.dto.NotificationRequest;
import ru.aston.notification.service.EmailService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void send (@Valid @RequestBody NotificationRequest request) {
        emailService.send(request.email(), request.type());
    }

}
