package com.example.webproject;

import com.example.webproject.models.User;

public class Helpers {
    public static User createMockUser() {
        var mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("TestUsername");
        mockUser.setPassword("TestPassword");
        mockUser.setLastName("TestLastName");
        mockUser.setFirstName("TestFirstName");
        mockUser.setEmail("test@email.com");
        return mockUser;
    }
}
