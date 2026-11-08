package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.service.EmailService;
import ru.aston.notification.controller.NotificationController;
import ru.aston.notification.NotificationServiceApplication;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@ContextConfiguration(classes = NotificationServiceApplication.class)
class NotificationControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean EmailService emailService;

    @Test
    void send_validRequest_returns202AndCallsService() throws Exception {
        String body = "{\"email\":\"user@test.com\",\"type\":\"CREATED\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        verify(emailService).send("user@test.com", UserEvent.UserEventType.CREATED);
    }

    @Test
    void send_invalidEmail_returns400AndDoesNotCallService() throws Exception {
        String body = "{\"email\":\"not-an-email\",\"type\":\"CREATED\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    void send_blankEmail_returns400() throws Exception {
        String body = "{\"email\":\"\",\"type\":\"CREATED\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    void send_nullType_returns400() throws Exception {
        String body = "{\"email\":\"user@test.com\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    void send_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }
}