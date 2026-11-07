package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.controller.UserController;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.exception.DuplicateEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.service.UserService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean UserService userService;

    private final UserResponse sample =
            new UserResponse(1L, "Ivan", "ivan@test.com", 25, LocalDateTime.now());

    @Test
    void getAll_ok() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sample));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ivan@test.com"));
    }

    @Test
    void getById_ok() throws Exception {
        when(userService.findById(1L)).thenReturn(sample);
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id 99 not found"));
    }

    @Test
    void create_created_withLocationHeader() throws Exception {
        when(userService.saveUser(any())).thenReturn(sample);
        String body = objectMapper.writeValueAsString(
                new CreateUserRequest("Ivan", "ivan@test.com", 25));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_duplicate_returns409() throws Exception {
        when(userService.saveUser(any()))
                .thenThrow(new DuplicateEmailException("User with this email already exists"));
        String body = objectMapper.writeValueAsString(
                new CreateUserRequest("Ivan", "dup@test.com", 25));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with this email already exists"));
    }

    @Test
    void create_invalidBody_returns400() throws Exception {
        String body = "{\"name\":\"\",\"email\":\"not-an-email\",\"age\":-1}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(any());
    }

    @Test
    void update_ok() throws Exception {
        when(userService.updateUser(eq(1L), any())).thenReturn(sample);
        String body = objectMapper.writeValueAsString(
                new UpdateUserRequest("Ivan", "ivan@test.com", 26));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}