package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.Comment;
import com.example.webproject.models.User;
import com.example.webproject.repositories.CommentRepository;
import com.example.webproject.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    public static final String BLOCKED_ACCOUNT_ERROR = "Your account has been blocked by an admin, currently you are not able to post,update or delete comments.";
    public static final String AUTHORIZATION_ERROR = "You are not able to update or delete other peoples comments.";
    private CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment getComment(int id) {
        return commentRepository.get(id);
    }

    @Override
    public void createComment(User user, Comment comment) {
        commentRepository.createComment(comment);
    }

    @Override
    public void updateComment(Comment comment, User user, int id) {
//        Comment repositoryComment = commentRepository.get(id);
//       if(repositoryComment.getUser().getId() == user.getId() || user.isAdmin()) {
//           if(user.isBlocked()){
//               throw new AuthorizationException(BLOCKED_ACCOUNT_ERROR);
//           }
//           comment.setUser(user);
//           commentRepository.updateComment(comment);
//        } else throw new AuthorizationException(AUTHORIZATION_ERROR);
        ValidationHelper.validateCommentExists(commentRepository, comment);
        ValidationHelper.checkIfBanned(user);
        ValidationHelper.validateModifyPermissions(commentRepository, comment, user);
        Comment commentToModify = commentRepository.get(comment.getId());
        comment.setPost(commentToModify.getPost());
        comment.setUser(user);
        commentRepository.updateComment(comment);
    }

    public List<Comment> getUserComments(User user) {
        List<Comment> userComments = commentRepository.getUserComments(user);
        return userComments;
    }

    @Override
    public void deleteComment(User user, int id) {
        Comment repositoryComment = commentRepository.get(id);
        ValidationHelper.validateModifyPermissions(commentRepository, repositoryComment, user);
        ValidationHelper.checkIfBanned(user);
        commentRepository.deleteComment(repositoryComment);
    }
}