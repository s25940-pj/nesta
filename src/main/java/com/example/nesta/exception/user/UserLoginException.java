package com.example.nesta.exception.user;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String message) {
        super(message);
    }
}
