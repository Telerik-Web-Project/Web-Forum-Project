package com.example.webproject.services;

import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.CommentRepository;
import com.example.webproject.services.contracts.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    @Override
    public Comment getComment(int id) {
        return commentRepository.get(id);
    }
    public List<Comment> getUserComments(User user) {
        return commentRepository.getUserComments(user);
    }
    @Override
    public void createComment(User user, Post post, Comment comment) {
        ValidationHelper.checkIfBanned(user);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.createComment(comment);
    }
    @Override
    public void updateComment(Comment comment, User user, int id) {
        ValidationHelper.validateCommentExists(commentRepository, comment);
        ValidationHelper.checkIfBanned(user);
        ValidationHelper.validateModifyPermissions(commentRepository, comment, user);
        Comment commentToModify = commentRepository.get(comment.getId());
        comment.setPost(commentToModify.getPost());
        comment.setUser(user);
        commentRepository.updateComment(comment);
    }
    @Override
    public void deleteComment(User user, int id) {
        Comment repositoryComment = commentRepository.get(id);
        ValidationHelper.validateModifyPermissions(commentRepository, repositoryComment, user);
        ValidationHelper.checkIfBanned(user);
        commentRepository.deleteComment(repositoryComment);
    }
}