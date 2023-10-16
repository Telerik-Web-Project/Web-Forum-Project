package com.example.webproject;

import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.HashSet;

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

    public static Post createMockPost() {
        var mockPost = new Post();
        mockPost.setId(1);
        mockPost.setTitle("TestTitle");
        mockPost.setPostCreator(createMockUser());
        mockPost.setContent("TestContent");
        mockPost.setPostComments(new HashSet<>());
        return mockPost;
    }
}
