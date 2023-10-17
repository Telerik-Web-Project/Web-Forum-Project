package com.example.webproject.repositories;

import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;
import java.util.Set;

public interface UserRepository {
    List <User> getAll(FilterOptions filterOptions);
    List <Post> getUserPosts(User user);
    User getById (int id);
    User getByUsername (String username);
    User getByEmail (String email);
    User createUser (User user);
    User updateUser (User user);
    User deleteUser (User user);

}
