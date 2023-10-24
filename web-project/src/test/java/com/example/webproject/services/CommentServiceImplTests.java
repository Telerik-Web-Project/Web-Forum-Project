package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.models.Comment;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.webproject.Helpers.createMockComment;
import static com.example.webproject.Helpers.createMockUser;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTests {
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    public void get_Should_returnComment_When_commentExists() {
        Comment comment = createMockComment();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        Comment result = commentService.getComment(1);

        Assertions.assertEquals(comment, result);
    }

    @Test
    public void create_Should_callRepository_When_UserIsNotBlocked(){
        Comment comment = createMockComment();
        User user = createMockUser();

        commentService.createComment(user,comment);

        Mockito.verify(commentRepository, Mockito.times(1))
                .createComment(comment);
    }

    @Test
    public void create_Should_throwException_When_userIsBlocked(){
        Comment comment = createMockComment();
        User user = createMockUser();
        user.setBlocked(true);

        Assertions.assertThrows(AuthorizationException.class,
                ()-> commentService.createComment(user,comment));
    }

    @Test
    public void update_Should_callRepository_When_commentExists() {
        Comment comment = createMockComment();

        Mockito.when(commentRepository.get(1))
                .thenReturn(comment);

        commentService.updateComment(comment,comment.getUser(),1);

        Mockito.verify(commentRepository, Mockito.times(1))
                        .updateComment(comment);
    }

    @Test
    public void update_Should_callRepository_When_UserIsCreator(){
        Comment comment = createMockComment();
        User user = comment.getUser();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        commentService.updateComment(comment, user,Mockito.anyInt());

        Mockito.verify(commentRepository, Mockito.times(1))
                .updateComment(comment);
    }

    @Test
    public void update_Should_callRepository_When_UserIsAdmin(){
        Comment comment = createMockComment();
        User user = new User();
        user.setAdmin(true);

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        commentService.updateComment(comment, user,Mockito.anyInt());

        Mockito.verify(commentRepository, Mockito.times(1))
                .updateComment(comment);
    }

    @Test
    public void update_Should_throwException_When_userIsNotCreatorOrAdmin(){
        Comment comment = createMockComment();
        User user = new User();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.updateComment(comment,user,Mockito.anyInt()));
    }

    @Test
    public void update_Should_throwException_When_userIsBlocked(){
        Comment comment = createMockComment();
        User user = comment.getUser();
        user.setBlocked(true);

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.updateComment(comment,user,Mockito.anyInt()));
    }

    @Test
    public void delete_Should_callRepository_When_userIsCreator(){
        Comment comment = createMockComment();
        User user = comment.getUser();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        commentService.deleteComment(user,Mockito.anyInt());

        Mockito.verify(commentRepository, Mockito.times(1))
                .deleteComment(comment);
    }

    @Test
    public void delete_Should_callRepository_When_userIsAdmin(){
        Comment comment = createMockComment();
        User user = new User();
        user.setAdmin(true);

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        commentService.deleteComment(user,Mockito.anyInt());

        Mockito.verify(commentRepository, Mockito.times(1))
                .deleteComment(comment);
    }

    @Test
    public void delete_Should_throwException_When_userIsNotCreatorOrAdmin(){
        Comment comment = createMockComment();
        User user = new User();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.deleteComment(user,Mockito.anyInt()));
    }

    @Test
    public void delete_Should_throwException_When_userIsBlocked(){
        Comment comment = createMockComment();
        User user = comment.getUser();
        user.setBlocked(true);

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(comment);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.deleteComment(user,Mockito.anyInt()));
    }

}
