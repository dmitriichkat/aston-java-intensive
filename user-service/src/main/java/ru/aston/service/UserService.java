package ru.aston.service;

import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse saveUser(CreateUserRequest createUserRequest);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest);

    void deleteUser(Long id);

}
