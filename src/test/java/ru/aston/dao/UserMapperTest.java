package ru.aston.dao;

import org.junit.jupiter.api.Test;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.entity.User;
import ru.aston.mapping.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_mapsAllFields() {
        CreateUserRequest req = new CreateUserRequest("Ivan", "ivan@test.com", 25);
        User user = mapper.toEntity(req);

        assertEquals("Ivan", user.getName());
        assertEquals("ivan@test.com", user.getEmail());
        assertEquals(25, user.getAge());
        assertNull(user.getId());
    }

    @Test
    void updateEntity_mapsAllFields() {
        User user = new User();
        user.setId(1L);
        UpdateUserRequest req = new UpdateUserRequest("Petr", "petr@test.com", 30);

        mapper.updateEntity(user, req);

        assertEquals(1L, user.getId());
        assertEquals("Petr", user.getName());
        assertEquals("petr@test.com", user.getEmail());
        assertEquals(30, user.getAge());
    }

    @Test
    void toResponse_mapsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@test.com");
        user.setAge(25);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        UserResponse resp = mapper.toResponse(user);

        assertEquals(1L, resp.id());
        assertEquals("Ivan", resp.name());
        assertEquals("ivan@test.com", resp.email());
        assertEquals(25, resp.age());
        assertEquals(now, resp.createdAt());
    }
}