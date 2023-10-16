package com.example.webproject.services;

import com.example.webproject.models.Comment;
import com.example.webproject.models.User;

public interface CommentService {
    Comment getComment(int id);

    void createComment(User user, Comment comment);

    void updateComment(Comment comment, User user, int id);

    void deleteComment(User user, int id);

}
