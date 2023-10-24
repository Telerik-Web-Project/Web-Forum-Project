package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;

import java.util.List;

public interface CommentRepository {

    Comment get (int id);
    void createComment (Comment comment);
    void updateComment (Comment comment);
    void deleteComment (Comment comment);
    List<Comment> getUserComments(User user);

    List<Post> getTenMostCommentedPosts();
}
