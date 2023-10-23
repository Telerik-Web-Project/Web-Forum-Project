package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.*;
import com.example.webproject.repositories.CommentRepositoryImpl;
import com.example.webproject.repositories.PostRepository;
import com.example.webproject.repositories.TagRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.example.webproject.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepositoryImpl commentRepository;

    @Mock
    private CommentServiceImpl commentService;

    @Mock
    private TagRepositoryImpl tagRepository;

    @InjectMocks
    private PostServiceImpl postService;



    @Test
    public void getAll_Should_callRepository(){
        PostFilter postFilter = createMockPostFilter();

        Mockito.when(postRepository.getAll(postFilter))
                .thenReturn(null);

        postService.getAll(postFilter);

        Mockito.verify(postRepository, Mockito.times(1))
                .getAll(postFilter);
    }

    @Test
    public void get_Should_returnPost_When_postExists(){
        Post mockPost = createMockPost();

        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenReturn(mockPost);

        Post post = postService.get(Mockito.anyInt());

        Assertions.assertEquals(mockPost, post);
    }

    @Test
    public void get_Should_callRepository(){
        Post mockPost = createMockPost();

        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenReturn(mockPost);

        postService.get(Mockito.anyInt());

        Mockito.verify(postRepository, Mockito.times(1))
                .get(Mockito.anyInt());
    }

    @Test
    public void get_Should_throwException_When_postDoesNotExist(){
        Post mockPost = createMockPost();

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.get(mockPost.getId()));
    }

    @Test
    public void getPostsWithTag_Should_callRepository(){
        Tag mockTag = createMockTag();
        List<Post> mockPostList = new ArrayList<>();


        Mockito.when(postRepository.getAll())
                        .thenReturn(mockPostList);

        Mockito.when(tagRepository.get(mockTag.getName()))
                        .thenReturn(mockTag);

        postService.getPostsWithTag(mockTag.getName());

        Mockito.verify(tagRepository, Mockito.times(1))
                .get(Mockito.anyString());
    }

    @Test
    public void addTagToPost_Should_callRepository_When_tagExists(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();
        mockPost.setPostCreator(mockUser);

        Mockito.when(tagRepository.get(Mockito.anyString()))
                .thenReturn(mockTag);

        Mockito.when(postRepository.updatePost(mockPost))
                .thenReturn(mockPost);

        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenReturn(mockPost);

        postService.addTagToPost(mockPost, mockTag, mockUser);


        Mockito.verify(postRepository,Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void addTagToPost_Should_callRepository_When_tagDoesNotExists(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();
        mockPost.setPostCreator(mockUser);


        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenReturn(mockPost);

        Mockito.when(tagRepository.get(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        Mockito.when(tagRepository.createTag(mockTag))
                .thenReturn(mockTag);

        postService.addTagToPost(mockPost, mockTag, mockUser);

        Mockito.verify(postRepository,Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void addTagToPost_Should_throwException_When_userIsBlocked(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();
        mockPost.setPostCreator(mockUser);
        mockUser.setBlocked(true);


        Assertions.assertThrows(UserBannedException.class,
                () -> postService.addTagToPost(mockPost, mockTag, mockUser));
    }

    @Test
    public void addTagToPost_Should_throwException_When_userIsNotAdminOrOwner(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();

        Mockito.when(postRepository.get(Mockito.anyInt()))
                        .thenThrow(AuthorizationException.class);

        Assertions.assertThrows(AuthorizationException.class,
                () -> postService.addTagToPost(mockPost, mockTag, mockUser));
    }

    @Test
    public void deleteTagFromPost_Should_callRepository(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();
        mockPost.setPostCreator(mockUser);

        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenReturn(mockPost);

        Mockito.when(tagRepository.get(Mockito.anyString()))
                .thenReturn(mockTag);

        Mockito.when(postRepository.updatePost(mockPost))
                .thenReturn(mockPost);


        postService.deleteTagFromPost(mockPost, mockUser, mockTag);


        Mockito.verify(postRepository,Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void _Should_throwException_When_userIsBlocked(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();
        mockPost.setPostCreator(mockUser);
        mockUser.setBlocked(true);


        Assertions.assertThrows(UserBannedException.class,
                () -> postService.deleteTagFromPost(mockPost, mockUser, mockTag));
    }

    @Test
    public void deleteTagFromPost_Should_throwException_When_userIsNotAdminOrOwner(){
        Post mockPost = createMockPost();
        Tag mockTag = createMockTag();
        User mockUser = createMockUser();

        Mockito.when(postRepository.get(Mockito.anyInt()))
                .thenThrow(AuthorizationException.class);

        Assertions.assertThrows(AuthorizationException.class,
                () -> postService.deleteTagFromPost(mockPost, mockUser, mockTag));
    }

    @Test
    public void createPost_Should_Call_Repository_When_PassValidations() {
        Post mockPost = createMockPost();

        postService.createPost(mockPost, mockPost.getPostCreator());

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(mockPost);
    }

    @Test
    public void createPost_Should_Throw_When_UserBanned() {
        Post mockPost = createMockPost();

        Mockito.when(postRepository.createPost(mockPost))
                .thenThrow(UserBannedException.class);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.createPost(mockPost, mockPost.getPostCreator()));

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(mockPost);
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
        Post mockPost = createMockPost();
        User mockUser = mockPost.getPostCreator();
        mockUser.setBlocked(true);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.updatePost(mockPost, mockUser));
    }

    @Test
    public void updatePost_Should_Throw_When_NoModifyPermissions() {
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setId(2);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        Assertions.assertThrows(AuthorizationException.class,
                () -> postService.updatePost(mockPost, mockUser));
    }

    @Test
    public void updatePost_Should_Throw_When_PostDoesNotExist() {
        Post mockPost = createMockPost();
        User moackUser = mockPost.getPostCreator();

        Mockito.when(postRepository.get(mockPost.getId()))
               .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(mockPost, moackUser));
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
        Post mockPost = createMockPost();
        User mockUser = mockPost.getPostCreator();
        mockUser.setBlocked(true);

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);

        Assertions.assertThrows(UserBannedException.class, () -> postService.deletePost(mockPost, mockUser));
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

    @Test
    public void deletePost_Should_Throw_When_PostDoesNotExist() {
        Post mockPost = createMockPost();
        User mockUser = mockPost.getPostCreator();

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.deletePost(mockPost, mockUser));
    }

    @Test
    public void getLikesCount_Should_CallRepository_When_PostIdExists() {
        Post mockPost = createMockPost();
        postService.createPost(mockPost, mockPost.getPostCreator());

        Mockito.when(postRepository.get(mockPost.getId()))
                .thenReturn(mockPost);
        Mockito.when(postRepository.getLikesCount(mockPost))
                .thenReturn(0);

        Assertions.assertEquals(0, postService.getLikesCount(mockPost));
    }

    @Test
    public void likePost_Should_callRepository(){
        Post mockPost = createMockPost();
        User mockUser = createMockUser();

        postService.likePost(mockUser, mockPost);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(mockPost);
    }

    @Test
    public void likePost_Should_throwException_When_userIsBlocked(){
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockUser.setBlocked(true);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.likePost(mockUser, mockPost));
    }

    @Test
    public void likePost_Should_removeLike_When_userAlreadyLikedThePost(){
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        mockPost.likePost(mockUser);

        postService.likePost(mockUser,mockPost);

        Assertions.assertEquals(0,mockPost.getLikes().size());
    }

    @Test
    public void getPostComments_should_CallRepository_When_Prompted() {
        Post mockPost = createMockPost();

        postService.getPostComments(mockPost);

        Mockito.verify(postRepository, Mockito.times(1))
                .getPostComments(mockPost);
    }

    @Test
    public void addComment_Should_callRepository(){
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        postService.addComment(mockUser, mockPost, mockComment);

        Mockito.verify(commentRepository, Mockito.times(1))
                .createComment(mockComment);
    }

    @Test
    public void addComment_Should_throwException_When_userIsBlocked(){
        Post mockPost = createMockPost();
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();
        mockUser.setBlocked(true);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.addComment(mockUser,mockPost,mockComment));
    }

    @Test
    public void updateComment_Should_callRepository(){
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenReturn(mockComment);

        postService.updateComment(mockUser, mockComment);

        Mockito.verify(commentRepository, Mockito.times(1))
                .updateComment(mockComment);
    }

    @Test
    public void updateComment_Should_throwException_When_userIsBlocked(){
        User mockUser = createMockUser();
        Comment mockComment = createMockComment();
        mockUser.setBlocked(true);

        Assertions.assertThrows(UserBannedException.class,
                () -> postService.updateComment(mockUser, mockComment));
    }

    @Test
    public void updateComment_Should_throwException_When_userIsNotAdminOrOwner() {
        Comment mockComment = createMockComment();
        User mockUser = createMockUser();
        mockComment.setUser(mockUser);
        User user = new User();

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                        .thenReturn(mockComment);

        Assertions.assertThrows(AuthorizationException.class, () -> postService.updateComment(user,mockComment));
    }

    @Test
    public void updateComment_Should_throwException_When_CommentDoesNotExists() {
        Comment mockComment = createMockComment();
        User mockUser = createMockUser();
        mockComment.setUser(mockUser);

        Mockito.when(commentRepository.get(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updateComment(mockUser,mockComment));
    }

    @Test
    public void getTenMostCommentedPosts_should_CallRepository_When_Prompted() {
        postService.getTenMostCommentedPosts();

        Mockito.verify(commentRepository, Mockito.times(1))
                .getTenMostCommentedPosts();

    }


}
