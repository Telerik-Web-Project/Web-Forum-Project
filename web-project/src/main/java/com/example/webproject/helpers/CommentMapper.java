package com.example.webproject.helpers;

import com.example.webproject.models.Comment;
import com.example.webproject.models.CommentDto;
import com.example.webproject.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final CommentRepository commentRepository;
    @Autowired
    public CommentMapper(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment fromCommentDto(int id, CommentDto dto) {
        Comment comment = fromDto(dto); // new Comment with content == content of DTO
        comment.setId(id);// setting the new comment id with the id of the @pathvariable
        Comment repositoryComment = commentRepository.get(id);// retrieving the existing comment form repo
        comment.setUser(repositoryComment.getUser());// setting new comment the same creator as the comment from repo
        comment.setPost(repositoryComment.getPost());// setting new comment the same post it belongs to as the
        // comment from repo
        return comment;
    }

    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        return comment;
    }



}
