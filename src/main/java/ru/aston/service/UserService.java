package ru.aston.service;

import ru.aston.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser (String name, String email, int age);

    List<User> findAll();

    Optional<User> findById (Long id);

    User updateUser(Long id, String name, String email, int age);

    boolean deleteUser (Long id);

}
