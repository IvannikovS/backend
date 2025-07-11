package ru.inline.ivannikov.backend.exception;

public class EntityNotSavedException extends RuntimeException{
    public EntityNotSavedException(String message) {
        super(message);
    }
}
