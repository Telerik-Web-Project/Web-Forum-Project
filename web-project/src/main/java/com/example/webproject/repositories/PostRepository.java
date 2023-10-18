package com.example.webproject.repositories;

import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;

import java.util.List;

public interface PostRepository {

    List <Post> getAll(UserFilter userFilter);
    Post get(int id);
    Post createPost (Post post);
    Post updatePost (Post post);
    Post deletePost (Post post);
    int getLikesCount(Post post);
}
