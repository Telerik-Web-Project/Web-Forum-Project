//package com.example.webproject.services;
//
//import com.example.webproject.exceptions.AuthorizationException;
//import com.example.webproject.exceptions.EntityNotFoundException;
//import com.example.webproject.exceptions.UserBannedException;
//import com.example.webproject.models.UserFilter;
//import com.example.webproject.models.Post;
//import com.example.webproject.models.User;
//import com.example.webproject.repositories.PostRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static com.example.webproject.Helpers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PostServiceImplTests {
//
//    @Mock
//    private PostRepository postRepository;
//
//    @InjectMocks
//    private PostServiceImpl postService;
//
//    @Test
//    public void getAll_Should_CallRepository() {
//        UserFilter mockUserFilter = createMockUserFilterOptions();
//        Mockito.when(postRepository.getAll(mockUserFilter))
//                .thenReturn(null);
//
//        postService.getAll(mockUserFilter);
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .getAll(mockUserFilter);
//    }
//
//    @Test
//    public void get_Should_CallRepository_When_PostIdExists() {
//        Post mockPost = createMockPost();
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        Post result = postService.get(mockPost.getId());
//
//        Assertions.assertEquals(mockPost, result);
//    }
//
//    @Test
//    public void get_Should_Throw_When_PostIdDoesNotExist() {
//        Post mockPost = createMockPost();
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenThrow(EntityNotFoundException.class);
//
//        Assertions.assertThrows(EntityNotFoundException.class,
//                () -> postService.get(mockPost.getId()));
//
////        Mockito.verify(postRepository, Mockito.times(1))
////                .get(mockPost.getId());
//    }
//
//    @Test
//    public void createPost_Should_Call_Repository_When_PassValidations() {
//        Post mockPost = createMockPost();
//
//        postService.createPost(mockPost, mockPost.getPostCreator());
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .createPost(mockPost);
//    }
//
//    @Test
//    public void createPost_Should_Throw_When_UserBanned() {
//        Post mockPost = createMockPost();
//
//        Mockito.when(postRepository.createPost(mockPost))
//                .thenThrow(UserBannedException.class);
//
//        Assertions.assertThrows(UserBannedException.class,
//                () -> postService.createPost(mockPost, mockPost.getPostCreator()));
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .createPost(mockPost);
//    }
//
//    @Test
//    public void updatePost_Should_Call_Repository_When_PassValidations() {
//        Post mockPost = createMockPost();
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        postService.updatePost(mockPost, mockPost.getPostCreator(), id);
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .updatePost(mockPost);
//    }
//
//    @Test
//    public void updatePost_Should_CallRepository_When_UserIsAdmin() {
//        Post mockPost = createMockPost();
//        User mockUser = createMockUser();
//        mockUser.setId(2);
//        mockUser.setAdmin(true);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        postService.updatePost(mockPost, mockUser, id);
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .updatePost(mockPost);
//    }
//
//    @Test
//    public void updatePost_Should_Throw_When_UserBanned() {
//        Post mockPost = createMockPost();
//        User mockUser = mockPost.getPostCreator();
//        mockUser.setBlocked(true);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        Assertions.assertThrows(UserBannedException.class, () -> postService.updatePost(mockPost, mockUser, id));
//    }
//
//    @Test
//    public void updatePost_Should_Throw_When_NoModifyPermissions() {
//        Post mockPost = createMockPost();
//        User mockUser = createMockUser();
//        mockUser.setId(2);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        Assertions.assertThrows(AuthorizationException.class, () -> postService.updatePost(mockPost, mockUser, id));
//    }
//
//    @Test
//    public void updatePost_Should_Throw_When_PostDoesNotExist() {
//        Post mockPost = createMockPost();
//        User moackUser = mockPost.getPostCreator();
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//               .thenThrow(EntityNotFoundException.class);
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(mockPost, moackUser, id));
//    }
//
//
//    @Test
//    public void deletePost_Should_Call_Repository_When_PassValidations() {
//        Post mockPost = createMockPost();
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        postService.deletePost(mockPost, mockPost.getPostCreator());
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .deletePost(mockPost);
//    }
//
//    @Test
//    public void deletePost_Should_CallRepository_When_UserIsAdmin() {
//        Post mockPost = createMockPost();
//        User mockUser = createMockUser();
//        mockUser.setId(2);
//        mockUser.setAdmin(true);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        postService.deletePost(mockPost, mockUser);
//
//        Mockito.verify(postRepository, Mockito.times(1))
//                .deletePost(mockPost);
//    }
//
//    @Test
//    public void deletePost_Should_Throw_When_UserBanned() {
//        Post mockPost = createMockPost();
//        User mockUser = mockPost.getPostCreator();
//        mockUser.setBlocked(true);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        Assertions.assertThrows(UserBannedException.class, () -> postService.deletePost(mockPost, mockUser));
//    }
//
//    @Test
//    public void deletePost_Should_Throw_When_NoModifyPermissions() {
//        Post mockPost = createMockPost();
//        User mockUser = createMockUser();
//        mockUser.setId(2);
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//
//        Assertions.assertThrows(AuthorizationException.class, () -> postService.deletePost(mockPost, mockUser));
//    }
//
//    @Test
//    public void deletePost_Should_Throw_When_PostDoesNotExist() {
//        Post mockPost = createMockPost();
//        User mockUser = mockPost.getPostCreator();
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenThrow(EntityNotFoundException.class);
//
//        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.deletePost(mockPost, mockUser));
//    }
//
//    @Test
//    public void getLikesCount_Should_CallRepository_When_PostIdExists() {
//        Post mockPost = createMockPost();
//        postService.createPost(mockPost, mockPost.getPostCreator());
//
//        Mockito.when(postRepository.get(mockPost.getId()))
//                .thenReturn(mockPost);
//        Mockito.when(postRepository.getLikesCount(mockPost))
//                .thenReturn(0);
//
//        Assertions.assertEquals(0, postService.getLikesCount(mockPost));
//    }
//}
