package com.example.webproject;

import com.example.webproject.models.*;

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
        return mockPost;
    }

    public static UserFilter createMockUserFilterOptions() {
        return new UserFilter("testFirstName",
                "testUsername",
                "testEmail",
                "",
                "");
    }

    public static PostFilter createMockPostFilterOptions() {
        return new PostFilter();
    }

    public static Comment createMockComment() {
        var mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setContent("TestContent");
        mockComment.setPost(createMockPost());
        mockComment.setUser(createMockUser());
        return mockComment;
    }
}
