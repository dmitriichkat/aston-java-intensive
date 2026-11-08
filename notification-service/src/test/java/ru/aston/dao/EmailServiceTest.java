package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.service.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock JavaMailSender mailSender;

    private EmailService service() {
        return new EmailService(mailSender, "noreply@aston.test", "Aston Account", "Aston");
    }

    @Test
    void send_created_buildsAndSendsCorrectMessage() {
        service().send("user@test.com", UserEvent.UserEventType.CREATED);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertArrayEquals(new String[]{"user@test.com"}, msg.getTo());
        assertEquals("noreply@aston.test", msg.getFrom());
        assertEquals("Aston Account", msg.getSubject());
        assertEquals("Здравствуйте! Ваш аккаунт на сайте Aston был успешно создан.", msg.getText());
    }

    @Test
    void send_deleted_buildsAndSendsCorrectMessage() {
        service().send("user@test.com", UserEvent.UserEventType.DELETED);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertEquals("Здравствуйте! Ваш аккаунт был удалён.", captor.getValue().getText());
    }

    @Test
    void buildText_created_containsSiteName() {
        assertEquals("Здравствуйте! Ваш аккаунт на сайте Aston был успешно создан.",
                service().buildText(UserEvent.UserEventType.CREATED));
    }

    @Test
    void buildText_deleted_returnsFixedText() {
        assertEquals("Здравствуйте! Ваш аккаунт был удалён.",
                service().buildText(UserEvent.UserEventType.DELETED));
    }

    @Test
    void send_propagatesExceptionFromMailSender() {
        doThrow(new MailSendException("smtp down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class,
                () -> service().send("user@test.com", UserEvent.UserEventType.CREATED));
    }
}