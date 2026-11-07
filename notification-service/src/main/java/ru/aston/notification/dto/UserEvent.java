package ru.aston.notification.dto;

public record UserEvent(Long id,
                        String email,
                        UserEventType type
) {
    public enum UserEventType {
        CREATED, DELETED
    }
}
