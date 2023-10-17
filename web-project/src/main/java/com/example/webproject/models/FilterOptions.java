package com.example.webproject.models;

import java.util.Optional;

public class FilterOptions {
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<String> email;

    public FilterOptions(String firstName, String lastName, String email) {
        this.firstName = Optional.ofNullable(firstName);
        this.lastName = Optional.ofNullable(lastName);
        this.email = Optional.ofNullable(email);

    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public Optional<String> getEmail() {
        return email;
    }
}
