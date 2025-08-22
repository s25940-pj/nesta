package com.example.nesta.exception.moveinapplication;

public class ActiveApplicationAlreadyExistsException extends RuntimeException {
    public ActiveApplicationAlreadyExistsException() {
        super("You already have an active application for this offer");
    }
}
