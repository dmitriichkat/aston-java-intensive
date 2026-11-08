package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.event.UserEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class UserServiceIntegrationIT {

    @MockitoBean UserEventPublisher userEventPublisher;

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private CreateUserRequest req(String email) {
        return new CreateUserRequest("Ivan", email, 25);
    }

    @Test
    void fullCrudFlow() {
        String email = "flow-" + UUID.randomUUID() + "@test.com";

        ResponseEntity<UserResponse> created =
                rest.postForEntity(url("/api/users"), req(email), UserResponse.class);
        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody().id());
        assertNotNull(created.getBody().createdAt());
        Long id = created.getBody().id();

        UserResponse fetched = rest.getForObject(url("/api/users/" + id), UserResponse.class);
        assertEquals(email, fetched.email());

        rest.delete(url("/api/users/" + id));
        assertEquals(404, rest.getForEntity(url("/api/users/" + id), String.class)
                .getStatusCode().value());
    }

    @Test
    void create_duplicateEmail_returns409() {
        String email = "dup-" + UUID.randomUUID() + "@test.com";
        assertEquals(201, rest.postForEntity(url("/api/users"), req(email), UserResponse.class)
                .getStatusCode().value());
        assertEquals(409, rest.postForEntity(url("/api/users"), req(email), String.class)
                .getStatusCode().value());
    }

    @Test
    void getById_notFound_returns404() {
        assertEquals(404, rest.getForEntity(url("/api/users/999999"), String.class)
                .getStatusCode().value());
    }
}