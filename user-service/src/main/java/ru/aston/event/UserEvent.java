package ru.aston.event;

public record UserEvent(
    Long id,
    String email,
    UserEventType type
) {
    public enum UserEventType {
        CREATED, DELETED
    }
}
