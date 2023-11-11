package com.example.webproject.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginDto {

    @NotEmpty(message = "Username can't be empty")
    @Size(min =4, max = 20, message = "Username must be between 4 and 23 characters")
    private String username;

    @NotEmpty(message = "Password can't be empty")
    @Size(min =6, max = 16, message = "Password must be between 6 and 16 characters")
    private String password;

    public LoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

