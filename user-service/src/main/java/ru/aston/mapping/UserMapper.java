package ru.aston.mapping;

import org.springframework.stereotype.Component;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.entity.User;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.name());
        user.setEmail(createUserRequest.email());
        user.setAge(createUserRequest.age());
        return user;
    }

    public void updateEntity(User user, UpdateUserRequest updateUserRequest) {
        user.setName(updateUserRequest.name());
        user.setEmail(updateUserRequest.email());
        user.setAge(updateUserRequest.age());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
