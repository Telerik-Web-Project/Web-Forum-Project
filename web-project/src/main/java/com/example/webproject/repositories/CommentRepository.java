package com.example.webproject.repositories;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;

public interface CommentRepository {

    Comment get (int id);
    Comment createComment (Comment comment);
    Comment updateComment (Comment comment);
    Comment deleteComment (Comment comment);

}
