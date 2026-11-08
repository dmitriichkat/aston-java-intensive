package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.kafka.UserEventListener;
import ru.aston.notification.service.EmailService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

    @Mock EmailService emailService;
    @InjectMocks
    UserEventListener listener;

    @Test
    void onUserEvent_created_delegatesToEmailService() {
        UserEvent event = new UserEvent(1L, "user@test.com", UserEvent.UserEventType.CREATED);

        listener.onUserEvent(event);

        verify(emailService).send("user@test.com", UserEvent.UserEventType.CREATED);
    }

    @Test
    void onUserEvent_deleted_delegatesToEmailService() {
        UserEvent event = new UserEvent(2L, "user2@test.com", UserEvent.UserEventType.DELETED);

        listener.onUserEvent(event);

        verify(emailService).send("user2@test.com", UserEvent.UserEventType.DELETED);
    }
}