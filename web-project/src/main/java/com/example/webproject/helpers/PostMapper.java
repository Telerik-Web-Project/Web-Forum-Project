package com.example.webproject.helpers;

import com.example.webproject.models.Post;
import com.example.webproject.models.PostDto;
import com.example.webproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserService userService;
    @Autowired
    public PostMapper(UserService userService) {
        this.userService = userService;
    }

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        // TODO: 16-Oct-23 check following logic
        post.setPostCreator(userService.getByUsername(postDto.getPostCreator().getUsername()));
        return post;
    }
}
