package com.example.webproject.exceptions;

public class AuthorizationException extends RuntimeException{

    public AuthorizationException(String message) {
        super(message);
    }
}
