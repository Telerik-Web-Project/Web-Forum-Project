package com.example.webproject.models;

import java.util.Optional;

public class FilterOptions {
    private Optional<String> firstName;
    private Optional<String> username;
    private Optional<String> email;

    public FilterOptions(String firstName, String username, String email) {
        this.firstName = Optional.ofNullable(firstName);
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);

    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getEmail() {
        return email;
    }
}
