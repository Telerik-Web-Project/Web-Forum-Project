package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.webproject.Helpers.createMockPost;
import static com.example.webproject.Helpers.createMockUser;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void getAll_Should_CallRepository() {
        Mockito.when(postRepository.getAll())
                .thenReturn(null);

        postService.getAll();

        Mockito.verify(postRepository, Mockito.times(1))
                .getAll();
    }

    @Test
    public void get_Should_CallRepository_When_PostIdExists() {
        Post post = createMockPost();

        Mockito.when(postRepository.get(post.getId()))
                .thenReturn(post);

        Post result = postService.get(post.getId());

        Assertions.assertEquals(post, result);
    }

    @Test
    public void get_Should_Throw_When_PostIdDoesNotExist() {
        Post post = createMockPost();

        Mockito.when(postRepository.get(post.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.get(post.getId()));

        Mockito.verify(postRepository, Mockito.times(1))
                .get(post.getId());
    }

    @Test
    public void createPost_Should_Call_Repository_When_PassValidations() {
        Post post = createMockPost();

        postService.createPost(post, post.getPostCreator());

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(post);
    }

    @Test
    public void createPost_Should_Throw_When_UserBanned() {
        Post post = createMockPost();

        Mockito.when(postRepository.createPost(post))
                .thenThrow(UserBannedException.class);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.createPost(post, post.getPostCreator()));

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(post);
    }

    @Test
    public void updatePost_Should_Call_Repository_When_PassValidations() {
        Post mockPost = createMockPost();
        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        postService.updatePost(mockPost, mockPost.getPostCreator());

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void updatePost_Should_CallRepository_When_UserIsAdmin() {
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setId(2);
        mockUser.setAdmin(true);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        postService.updatePost(mockPost, mockUser);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void updatePost_Should_Throw_When_UserBanned() {
        Post post = createMockPost();
        User user = post.getPostCreator();
        user.setBlocked(true);

        Mockito.when(postRepository.get(post.getId()))
                .thenReturn(post);

        Assertions.assertThrows(UserBannedException.class, () -> postService.updatePost(post, user));
    }

    @Test
    public void updatePost_Should_Throw_When_NoModifyPermissions() {
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setId(2);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        Assertions.assertThrows(AuthorizationException.class, () -> postService.updatePost(mockPost, mockUser));
    }

    @Test
    public void deletePost_Should_Call_Repository_When_PassValidations() {
        Post mockPost = createMockPost();
        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        postService.deletePost(mockPost, mockPost.getPostCreator());

        Mockito.verify(postRepository, Mockito.times(1))
                .deletePost(mockPost);
    }

    @Test
    public void deletePost_Should_CallRepository_When_UserIsAdmin() {
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setId(2);
        mockUser.setAdmin(true);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        postService.deletePost(mockPost, mockUser);

        Mockito.verify(postRepository, Mockito.times(1))
                .deletePost(mockPost);
    }

    @Test
    public void deletePost_Should_Throw_When_UserBanned() {
        Post post = createMockPost();
        User user = post.getPostCreator();
        user.setBlocked(true);

        Mockito.when(postRepository.get(post.getId()))
                .thenReturn(post);

        Assertions.assertThrows(UserBannedException.class, () -> postService.deletePost(post, user));
    }

    @Test
    public void deletePost_Should_Throw_When_NoModifyPermissions() {
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setId(2);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        Assertions.assertThrows(AuthorizationException.class, () -> postService.deletePost(mockPost, mockUser));
    }

}
