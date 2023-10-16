package com.example.webproject.repositories;

import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;
import java.util.Set;

public interface UserRepository {
    List <User> getAll(String username, String firstname, String email);
    List <Post> getUserPosts(int userId);
    User getById (int id);
    User getByUsername (String username);
    User getByEmail (String email);
    User createUser (User user);
    User updateUser (User user);
    User deleteUser (User user);

}
