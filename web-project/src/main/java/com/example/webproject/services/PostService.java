package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface PostService {
    List<Post> getAll(FilterOptions filterOptions);

    Post get(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(Post post, User user);
}
