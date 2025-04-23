package com.example.nesta.exception;

public class InvalidQueryParamException extends RuntimeException{
    public InvalidQueryParamException(String param) {
        super("Parameter " + param + " is not allowed.");
    }
}
