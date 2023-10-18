package com.example.webproject.controllers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.helpers.UserMapper;
import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.models.UserDto;
import com.example.webproject.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<User> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        FilterOptions filterOptions = new FilterOptions(firstName, username, email, sortBy, sortOrder);
        return userService.getAll(filterOptions);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        try {
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping("/{id}/posts")
    public List<Post> getUserPosts(@PathVariable int id, @RequestHeader HttpHeaders headers) {
    try {
          User loggedUser = authenticationHelper.getUser(headers);
          User userToCheckPosts = userService.getById(id);
          return userService.getUserPosts(loggedUser, userToCheckPosts);
        }   catch (EntityNotFoundException e){
                 throw  new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }   catch (AuthorizationException e){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());}
    }
    @PutMapping()
    public User updateUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserDto userDto){
      try {
          User user = authenticationHelper.getUser(headers);
          User userToBeUpdate = userMapper.fromDtoToUser(userDto);
          userService.updateUser(user, userToBeUpdate);
          return userToBeUpdate;
      }catch (EntityNotFoundException e){
          throw  new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
      }   catch (AuthorizationException e){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());}
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id){
       try {
           User userLoggedUser = authenticationHelper.getUser(headers);
           User userToBeDelete = userService.getById(id);
           userService.deleteUser(userLoggedUser, userToBeDelete);
       }catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
       }catch (AuthorizationException e){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
       }
    }
    @PostMapping()
    public User createUser(@Valid @RequestBody UserDto userDto){
       try {
           User userToCreate = userMapper.fromDtoToUser(userDto);
           userService.createUser(userToCreate);
           return userToCreate;
       }catch (EntityDuplicateException e){
           throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());
       }
    }
}


