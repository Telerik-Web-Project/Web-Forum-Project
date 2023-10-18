package com.example.webproject.services;
import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface UserService {
    List <User> getAll(FilterOptions filterOptions);
    User getById (int id);
    User getByUsername (String username);
    void createUser (User user);
    List <Post> getUserPosts(User loggedUser,User user);
    void updateUser (User loggedUser,User userToBeUpdated);
    void deleteUser (User logeedUser,User userToBeDeleted);
}
