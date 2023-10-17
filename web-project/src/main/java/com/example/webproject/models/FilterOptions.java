package com.example.webproject.models;

import java.util.Optional;

public class FilterOptions {
    private Optional<User> creator;

    public FilterOptions(User creator) {
        this.creator = Optional.ofNullable(creator);
    }

    public Optional<User> getCreator() {
        return creator;
    }

    public void setCreator(Optional<User> creator) {
        this.creator = creator;
    }
}
