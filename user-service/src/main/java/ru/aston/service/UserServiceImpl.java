package ru.aston.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.entity.User;
import ru.aston.event.UserEvent;
import ru.aston.event.UserEventPublisher;
import ru.aston.exception.DuplicateEmailException;
import ru.aston.exception.UserNotFoundException;
import ru.aston.mapping.UserMapper;
import ru.aston.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";
    private static final String DUPLICATE_EMAIL_MESSAGE = "User with this email already exists";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    @Transactional
    public UserResponse saveUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            logger.warn("Found already existing user with this email while saving");
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        }
        User saved = userRepository.saveAndFlush(userMapper.toEntity(createUserRequest));
        userEventPublisher.publish(new UserEvent(saved.getId(), saved.getEmail(), UserEvent.UserEventType.CREATED));
        logger.info("User with Id {} created", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        boolean emailTakenByAnotherUser = userRepository.findByEmail(updateUserRequest.email())
                .filter(other -> !other.getId().equals(id))
                .isPresent();
        if (emailTakenByAnotherUser) {
            logger.warn("Found already existing user with this email while updating");
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        }

        userMapper.updateEntity(user, updateUserRequest);
        User saved = userRepository.saveAndFlush(user);
        logger.info("User with Id {} updated", id);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("User with id {} not found", id);
            throw new UserNotFoundException(id);
        }
        Optional<User> deleted = userRepository.findById(id);
        userRepository.deleteById(id);
        userEventPublisher.publish(new UserEvent(deleted.get().getId(), deleted.get().getEmail(), UserEvent.UserEventType.DELETED));
        logger.info("User with Id {} deleted", id);
    }
}
