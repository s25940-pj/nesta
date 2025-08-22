package com.example.nesta.exception.moveinapplication;

import com.example.nesta.exception.common.ResourceNotFoundException;

public class MoveInApplicationNotFoundException extends ResourceNotFoundException {
    public MoveInApplicationNotFoundException(Long id) {
        super("Move in application with id " + id + " not found");
    }
}
