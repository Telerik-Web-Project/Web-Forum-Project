package com.example.webproject.controllers;

import com.example.webproject.dtos.CommentDto;
import com.example.webproject.dtos.PostDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.helpers.CommentMapper;
import com.example.webproject.helpers.PostMapper;
import com.example.webproject.models.*;
import com.example.webproject.services.contracts.CommentService;
import com.example.webproject.services.contracts.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public PostController(AuthenticationHelper authenticationHelper, PostMapper postMapper, PostService postService, CommentService commentService, CommentMapper commentMapper) {
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.postService = postService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    //TODO implement get10mostLikedPosts
    @GetMapping()
    public List<Post> getAll(@RequestHeader(required = false) HttpHeaders headers,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String content,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) String sortOrder) {
        PostFilter filter = new PostFilter(title,content,sortBy,sortOrder);
        return postService.getAll(filter);
    }

    @GetMapping("/mostCommented")
    public List<Post> getTenMostCommentedPosts () {
        return commentService.getTenMostCommentedPosts();
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable int id){
        try {
            return postService.get(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/tags")
    public List<Post> getPostsWithTag(@RequestParam String tag){
      try {
          return postService.getPostsWithTag(tag);
      }catch (EntityNotFoundException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
      }

    }
    @PostMapping("/{id}/tags")
    public void addTagToPost(@PathVariable int id, @RequestBody Tag tag, @RequestHeader HttpHeaders headers){
        try {
            User loggedUser = authenticationHelper.getUser(headers);
            Post post = postService.get(id);
            postService.addTagToPost(post, tag, loggedUser);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/tags")
    public void deleteTagFromPost(@PathVariable int id,
                                  @RequestHeader HttpHeaders headers,
                                  @RequestBody Tag tag){
        try{
            User loggedUser = authenticationHelper.getUser(headers);
            Post post = postService.get(id);
            postService.deleteTagFromPost(post, loggedUser, tag);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public List<Comment> getPostComments(@PathVariable int id) {
        try {
            Post post = postService.get(id);
            postService.getPostComments(post);
            return postService.getPostComments(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    public Post createPost(@Valid @RequestBody PostDto postDto, @RequestHeader HttpHeaders httpHeader) {
        try {
            Post postToCreate = postMapper.fromDto(postDto);
            User loggedUser = authenticationHelper.getUser(httpHeader);
            postService.createPost(postToCreate, loggedUser);
            return postToCreate;
        } catch (EntityDuplicateException | UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable int id, @RequestHeader HttpHeaders headers, @Valid @RequestBody PostDto postDto) {
        try {
            User user = authenticationHelper.getUser(headers);
            Post post = postMapper.fromDto(postDto);
            post.setId(id);
            post.setPostCreator(user);
            postService.updatePost(post, user);
            return post;

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityDuplicateException | UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("{id}/like")
    public void likePost(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.getUser(headers);
            Post post = postService.get(id);
            postService.likePost(user, post);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deletePost(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authenticationHelper.getUser(headers);
            Post postToDelete = postService.get(id);
            postService.deletePost(postToDelete, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{id}/comments")
    public void addComment(@PathVariable int id, @RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto) {
        try {
            Comment comment = commentMapper.fromDto(commentDto);
            User user = authenticationHelper.getUser(headers);
            Post post = postService.get(id);
            postService.addComment(user, post, comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/comments/{id}")
    public void updateComment(@PathVariable int id, @RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto) {
        try {
            Comment comment = commentMapper.fromDto(commentDto);
            comment.setId(id);
            User user = authenticationHelper.getUser(headers);
            commentService.updateComment(comment, user, comment.getId());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.getUser(headers);
            commentService.deleteComment(user, id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UserBannedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
