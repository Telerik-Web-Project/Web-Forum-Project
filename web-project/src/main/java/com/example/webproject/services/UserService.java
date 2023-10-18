package com.example.webproject.services;
import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface UserService {
    List <User> getAll(UserFilter userFilter);
    User getById (int id);
    User getByUsername (String username);
    void createUser (User user);
    List <Post> getUserPosts(User loggedUser,User user);
    void updateUser (User user,User userToBeUpdated);
    void deleteUser (User user,User userToBeDeleted);
    void changeBanStatus(User user);
}
