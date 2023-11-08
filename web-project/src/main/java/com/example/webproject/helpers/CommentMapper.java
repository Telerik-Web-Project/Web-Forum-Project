package com.example.webproject.helpers;

import com.example.webproject.models.Comment;
import com.example.webproject.dtos.CommentDto;
import com.example.webproject.repositories.contracts.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentMapper() {
    }
    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        return comment;
    }
    public Comment fromDto(CommentDto dto,int id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(dto.getContent());
        return comment;
    }


}
