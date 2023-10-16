package com.example.webproject.repositories;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface PostRepository {

    List <Post> getAll(); //TODO filter most recent ?
    List <Comment> getPostComments(Post post);
    Post get(int id);
    Post createPost (Post post);
    Post updatePost (Post post);
    Post deletePost (Post post);

}
