package com.example.webproject.services;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostFilter;
import com.example.webproject.models.User;

import java.util.List;

public interface PostService {
    List<Post> getAll(PostFilter filter);

    Post get(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(Post post, User user);

    void likePost(User user, Post post);

    void addComment(User user, Post post, Comment comment);
    List<Post> getPostsAsAnonymousUser();
    List<Comment> getPostComments(Post post);
}
