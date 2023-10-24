package com.example.webproject.dtos;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateUserDto {
    @NotNull(message = "You sure have a name, right?")
    @Size(min =4, max = 32, message = "First name must be between 4 and 32 characters")
    private String firstName;
    @NotNull(message = "We really need your last name here!")
    @Size(min =4, max = 32, message = "Last name must be between 4 and 32 characters")
    private String lastName;

    @NotNull(message = "email address required")
    private String email;

    @NotNull(message = "password required")
    private String password;

    public UpdateUserDto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
