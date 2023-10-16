package com.example.webproject.services;

import com.example.webproject.models.Post;
import com.example.webproject.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.webproject.Helpers.createMockPost;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    private void getAll_Should_ReturnAll_When_PostExists() {
        Post post = createMockPost();
        Post post2 = createMockPost();
        postRepository.createPost(post);
        postRepository.createPost(post2);

        postService.getAll();
        Mockito.verify(postRepository, Mockito.times(1)).getAll();
    }
}
