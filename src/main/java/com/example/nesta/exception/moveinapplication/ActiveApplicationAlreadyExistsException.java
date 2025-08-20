package com.example.nesta.exception.moveinapplication;

public class ActiveApplicationAlreadyExistsException extends RuntimeException {
    public ActiveApplicationAlreadyExistsException(String message) {
        super(message);
    }
}
