package com.example.webproject.helpers;

import com.example.webproject.models.Post;
import com.example.webproject.models.PostDto;
import com.example.webproject.services.PostService;
import com.example.webproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserService userService;
    private PostService postService;

    @Autowired
    public PostMapper(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public Post fromDto(int id, PostDto postDto) {
        Post post = new Post();
        post.setId(id);
        Post repositoryPost = postService.get(id);
        post.setPostCreator(repositoryPost.getPostCreator());
        return post;
    }

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPostCreator(userService.getByUsername(postDto.getPostCreator().getUsername()));
        return post;
    }
}
