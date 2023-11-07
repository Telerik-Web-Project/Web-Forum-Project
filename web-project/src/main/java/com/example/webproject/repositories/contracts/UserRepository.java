package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Phone;
import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface UserRepository {
    List <User> getAll(UserFilter userFilter);
    List <Post> getUserPosts(User user);
    User getById (int id);
    User getByUsername (String username);
    User getByEmail (String email);
    User createUser (User user);
    User updateUser (User user);
    User deleteUser (User user);
    int getUsersCount();
    List<User> getPaginatedUsers(int page, int postPerPage);
}
