package com.example.webproject.services;
import com.example.webproject.models.User;

import java.util.List;

public interface UserService {
    User getById (int id);
    User getByUsername (String username);
    void createUser (User user);
    void updateUser (User user,User userToBeUpdated);
    void deleteUser (User user,User userToBeDeleted);
}
