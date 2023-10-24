package com.example.webproject.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PhoneDto {
    @NotNull
    @Size(min = 10, max = 20, message = "Invalid phone number length")
    private String phoneNumber;

    public PhoneDto() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
