package ru.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Данные пользователя")
public record UserResponse(
    @Schema(description = "Уникальный идентификатор", example = "1")
    Long id,
    @Schema(description = "Имя пользователя", example = "Иван")
    String name,
    @Schema(description = "Email пользователя", example = "ivan@example.com")
    String email,
    @Schema(description = "Возраст", example = "25")
    Integer age,
    @Schema(description = "Дата и время создания", example = "2026-01-01T00:00:00")
    LocalDateTime createdAt
) { }
