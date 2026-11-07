package ru.aston.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.entity.User;
import ru.aston.exception.DuplicateEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.mapping.UserMapper;
import ru.aston.repository.UserRepository;
import ru.aston.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setName("Ivan");
        u.setEmail("ivan@test.com");
        u.setAge(25);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }

    private UserResponse response(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt());
    }

    @Test
    void saveUser_success() {
        User u = user();
        CreateUserRequest req = new CreateUserRequest("Ivan", "ivan@test.com", 25);
        when(userRepository.existsByEmail("ivan@test.com")).thenReturn(false);
        when(userMapper.toEntity(req)).thenReturn(u);
        when(userRepository.saveAndFlush(u)).thenReturn(u);
        when(userMapper.toResponse(u)).thenReturn(response(u));

        UserResponse result = userService.saveUser(req);

        assertEquals(1L, result.id());
        verify(userRepository).existsByEmail("ivan@test.com");
        verify(userRepository).saveAndFlush(u);
    }

    @Test
    void saveUser_duplicateEmail_throws_andDoesNotSave() {
        CreateUserRequest req = new CreateUserRequest("Ivan", "dup@test.com", 25);
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.saveUser(req));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void findById_success() {
        User u = user();
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userMapper.toResponse(u)).thenReturn(response(u));

        UserResponse result = userService.findById(1L);

        assertEquals("ivan@test.com", result.email());
    }

    @Test
    void findById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void findAll_returnsMappedList() {
        User u = user();
        when(userRepository.findAll()).thenReturn(List.of(u));
        when(userMapper.toResponse(u)).thenReturn(response(u));

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAll_emptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        assertTrue(userService.findAll().isEmpty());
    }

    @Test
    void updateUser_success() {
        User u = user();
        UpdateUserRequest req = new UpdateUserRequest("Ivan2", "ivan2@test.com", 26);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.findByEmail("ivan2@test.com")).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(u)).thenReturn(u);
        when(userMapper.toResponse(u)).thenReturn(
                new UserResponse(1L, "Ivan2", "ivan2@test.com", 26, u.getCreatedAt()));

        UserResponse result = userService.updateUser(1L, req);

        assertEquals("ivan2@test.com", result.email());
        verify(userMapper).updateEntity(u, req);
    }

    @Test
    void updateUser_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        UpdateUserRequest req = new UpdateUserRequest("x", "x@t.com", 20);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, req));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateUser_emailTakenByAnother_throws() {
        User current = user();
        User other = new User();
        other.setId(2L);
        other.setEmail("taken@test.com");

        UpdateUserRequest req = new UpdateUserRequest("Ivan", "taken@test.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(other));

        assertThrows(DuplicateEmailException.class, () -> userService.updateUser(1L, req));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateUser_sameEmailSameUser_ok() {
        User current = user();
        UpdateUserRequest req = new UpdateUserRequest("Ivan", "ivan@test.com", 26);
        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.findByEmail("ivan@test.com")).thenReturn(Optional.of(current));
        when(userRepository.saveAndFlush(current)).thenReturn(current);
        when(userMapper.toResponse(current)).thenReturn(
                new UserResponse(1L, "Ivan", "ivan@test.com", 26, current.getCreatedAt()));

        assertNotNull(userService.updateUser(1L, req));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound_throws() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}