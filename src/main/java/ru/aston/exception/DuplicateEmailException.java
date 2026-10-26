package ru.aston.exception;

public class DuplicateEmailException extends DaoException {

    public DuplicateEmailException(String message, Throwable cause ) {
        super(message, cause);
    }
}
