package ru.aston.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    String email,

    @NotNull(message = "Age cannot be null")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    Integer age
) {
}
