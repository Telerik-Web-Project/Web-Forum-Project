package com.example.webproject.services;
import com.example.webproject.models.Phone;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.models.UserFilter;

import java.util.List;

public interface UserService {
    List <User> getAll(UserFilter userFilter);
    User getById (int id);
    User getByUsername (String username);
    void createUser (User user);
    List <Post> getUserPosts(User loggedUser,User user);
    User updateUser (User loggedUser, User userToBeUpdated);
    void deleteUser (User loggedUser,User userToBeDeleted);
    void changeBanStatus(User user);

    void changeAdminStatus(User user);

    void addPhoneNumber(Phone phone);

    void updatePhoneNumber(User user, Phone phone);

    void deletePhoneNumber(User user);
}
