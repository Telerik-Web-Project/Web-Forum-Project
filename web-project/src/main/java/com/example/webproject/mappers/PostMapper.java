package com.example.webproject.mappers;

import com.example.webproject.dtos.UpdatePostDto;
import com.example.webproject.models.Post;
import com.example.webproject.dtos.PostDto;
import com.example.webproject.models.User;
import com.example.webproject.services.contracts.PostService;
import com.example.webproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostMapper() {
    }

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return post;
    }
    public Post fromUpdatePostDto(UpdatePostDto updatePostDto, int id, User user) {
        Post post = new Post();
        post.setId(id);
        post.setPostCreator(user);
        post.setTitle(updatePostDto.getTitle());
        post.setContent(updatePostDto.getContent());
        return post;
    }
}
