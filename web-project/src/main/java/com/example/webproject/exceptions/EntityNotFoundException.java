package com.example.webproject.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(int id){
        super(String.format("There is no specific information related to User with id %d",id));
    }
    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }
}
