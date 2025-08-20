package com.example.nesta.exception.moveinapplication;

public class MoveInApplicationAlreadyClosedException extends RuntimeException {
  public MoveInApplicationAlreadyClosedException(Long id) {
    super("Move-in application with id " + id + " is already closed.");
  }
}