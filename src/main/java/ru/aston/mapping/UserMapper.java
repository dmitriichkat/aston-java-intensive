package ru.aston.mapping;

import org.springframework.stereotype.Component;
import ru.aston.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto) {
        User user = new User();
        updateEntity(user, dto);
        return user;
    }

    public void updateEntity(User user, UserRequestDto dto) {
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
    }

    public UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
