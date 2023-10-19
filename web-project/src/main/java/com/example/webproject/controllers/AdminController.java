package com.example.webproject.controllers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.models.User;
import com.example.webproject.models.UserFilter;
import com.example.webproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AdminController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<User> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        UserFilter userFilter = new UserFilter(firstName, username, email, sortBy, sortOrder);
        return userService.getAll(userFilter);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return userService.getById(id);
    }

    @PutMapping("/user/{id}/ban")
    public void changeBlockStatus(@RequestHeader HttpHeaders httpHeaders, @PathVariable int id) {
        try {
            User user = userService.getById(id);
            checkAccessPermissions(authenticationHelper.getUser(httpHeaders));
            userService.changeBanStatus(user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/user/{id}/makeAdmin")
    public void changeAdminStatus(@RequestHeader HttpHeaders httpHeaders, @PathVariable int id) {
        try {
            User user = userService.getById(id);
            checkAccessPermissions(authenticationHelper.getUser(httpHeaders));
            userService.changeAdminStatus(user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    private static void checkAccessPermissions(User executingUser) {
        if (!executingUser.isAdmin()) {
            throw new AuthorizationException("Administrator privileges needed");
        }
    }
}
