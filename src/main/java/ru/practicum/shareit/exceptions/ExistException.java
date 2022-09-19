package ru.practicum.shareit.exceptions;

public class ExistException extends RuntimeException {
    String error;

    public ExistException(String error) {
        super(error);
    }
}
