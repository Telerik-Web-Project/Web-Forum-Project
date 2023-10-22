package com.example.webproject.services;

import com.example.webproject.models.Comment;
import com.example.webproject.models.User;

import java.util.List;

public interface CommentService {
    Comment getComment(int id);

    void createComment(User user, Comment comment);

    public List<Comment> getUserComments(User user);

    void updateComment(Comment comment, User user, int id);

    void deleteComment(User user, int id);

}
