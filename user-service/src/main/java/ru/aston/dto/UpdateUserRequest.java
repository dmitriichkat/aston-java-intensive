package ru.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на обновление данных пользователя")
public record UpdateUserRequest(
    @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 2, max = 100)
    String name,

    @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Email
    @Size(max = 100)
    String email,

    @Schema(description = "Возраст", example = "25", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1)
    @Max(value = 120)
    Integer age
) {
}
