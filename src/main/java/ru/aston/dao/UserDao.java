package ru.aston.dao;

import ru.aston.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User save(User user);

    List <User> findAll();

    Optional <User> findById (Long id);

    User update(User user);

    boolean delete(Long id);
}
