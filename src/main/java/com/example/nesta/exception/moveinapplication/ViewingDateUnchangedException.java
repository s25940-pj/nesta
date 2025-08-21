package com.example.nesta.exception.moveinapplication;

public class ViewingDateUnchangedException extends RuntimeException {
    public ViewingDateUnchangedException() {
        super("New viewing date must be different from the current one.");
    }
}
