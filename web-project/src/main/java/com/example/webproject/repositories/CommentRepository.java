package com.example.webproject.repositories;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;

public interface CommentRepository {

    Comment get (int id);
    void createComment (Comment comment);
    void updateComment (Comment comment);
    void deleteComment (Comment comment);

}
