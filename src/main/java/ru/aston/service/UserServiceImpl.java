package ru.aston.service;

import ru.aston.dao.UserDao;
import ru.aston.dao.UserDaoImpl;
import ru.aston.entity.User;
import ru.aston.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public User saveUser(String name, String email, int age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userDao.save(user);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User updateUser(Long id, String name, String email, int age) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userDao.delete(id);
    }
}
