package ru.aston.notification.service;

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

    public void send (String email, UserEvent.UserEventType type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(buildText(type));
        javaMailSender.send(message);
        logger.info("Email about {} sent", type);
    }

    public String buildText(UserEvent.UserEventType type) {
        return switch (type) {
            case CREATED -> "Здравствуйте! Ваш аккаунт на сайте " + siteName + " был успешно создан.";
            case DELETED -> "Здравствуйте! Ваш аккаунт был удалён.";
        };
    }

}
