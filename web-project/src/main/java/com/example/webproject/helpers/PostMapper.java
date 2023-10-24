package com.example.webproject.helpers;

import com.example.webproject.models.Post;
import com.example.webproject.dtos.PostDto;
import com.example.webproject.services.contracts.PostService;
import com.example.webproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public PostMapper(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return post;
    }
}
