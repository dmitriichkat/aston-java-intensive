package ru.aston.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
    @NotBlank
    @Size(min = 2, max = 100)
    String name,

    @NotBlank
    @Email
    @Size(max = 100)
    String email,

    @NotNull
    @Min(value = 1)
    @Max(value = 120)
    Integer age
) {
}
