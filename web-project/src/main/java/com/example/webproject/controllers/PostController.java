package com.example.webproject.controllers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.helpers.PostMapper;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostDto;
import com.example.webproject.models.User;
import com.example.webproject.models.UserDto;
import com.example.webproject.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final PostService postService;

    public PostController(AuthenticationHelper authenticationHelper, PostMapper postMapper, PostService postService) {
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.postService = postService;
    }
    //TODO implement getTheTenRecentPosts
    //TODO implement get10mostLikedPosts

    @PostMapping()
    public Post createPost(@Valid @RequestBody PostDto postDto, @RequestHeader HttpHeaders httpHeader){
        try {
            Post postToCreate = postMapper.fromDto(postDto);
            User loggedUser = authenticationHelper.getUser(httpHeader);
            postService.createPost(postToCreate, loggedUser);
            return postToCreate;
        }catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());
        }catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable int id,@RequestHeader HttpHeaders headers, @Valid @RequestBody PostDto postDto){
        try {
            User user = authenticationHelper.getUser(headers);
            Post post = postMapper.fromDto(postDto);
            post.setId(id);
            postService.updatePost(post, user);
            return post;

        }catch (EntityNotFoundException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }   catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());}
        catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());
        }
    }
    @PutMapping("{id}/like")
    public void likePost(@RequestHeader HttpHeaders headers,@PathVariable int id){
        try{
            User user = authenticationHelper.getUser(headers);
            Post post = postService.get(id);
            postService.likePost(user,post);
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }catch (EntityNotFoundException e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
    }
    }
    @DeleteMapping("/{id}")
    public void deletePost(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            User loggedUser = authenticationHelper.getUser(headers);
            Post postToDelete = postService.get(id);
            postService.deletePost(postToDelete, loggedUser);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

}
