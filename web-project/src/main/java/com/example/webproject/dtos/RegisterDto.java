package com.example.webproject.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterDto extends LoginDto{

    @NotEmpty(message = "First Name cannot be empty")
    @Size(min =4, max = 32, message = "First name must be between 4 and 32 characters")
    private String firstName;
    @NotEmpty(message = "Last Name cannot be empty")
    @Size(min =4, max = 32, message = "Last name must be between 4 and 32 characters")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min =6, max = 16, message = "Password must be between 6 and 16 characters")
    private String confirmPassword;

    public RegisterDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
