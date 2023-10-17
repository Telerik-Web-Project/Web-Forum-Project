package com.example.webproject.repositories;

import com.example.webproject.models.Comment;
import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface PostRepository {

    List <Post> getAll(FilterOptions filterOptions); //TODO filter most recent ?
    List <Comment> getAllComments();
    Post get(int id);
    Post createPost (Post post);
    Post updatePost (Post post);
    Post deletePost (Post post);
    int getLikesCount(Post post);
}
