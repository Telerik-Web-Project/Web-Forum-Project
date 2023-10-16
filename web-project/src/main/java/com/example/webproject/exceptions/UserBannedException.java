package com.example.webproject.exceptions;

public class UserBannedException extends RuntimeException {
    public UserBannedException() {
        super("User is currently banned.");
    }
}
