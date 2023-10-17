package com.example.webproject.models;

import java.util.Optional;

public class FilterOptions {
    private Optional<String> firstName;
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> sortedBy;
    private Optional<String> sortOrder;



    public FilterOptions(String firstName, String username, String email, String sortedBy,String sortOrder) {
        this.firstName = Optional.ofNullable(firstName);
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.sortedBy = Optional.ofNullable(sortedBy);
        this.sortOrder = Optional.ofNullable(sortOrder);

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
    public Optional<String> getSortedBy() {
        return sortedBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
