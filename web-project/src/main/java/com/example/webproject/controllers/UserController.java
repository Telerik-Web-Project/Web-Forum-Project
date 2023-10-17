package com.example.webproject.controllers;

import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.User;
import com.example.webproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public User get(@PathVariable int id){
        return userService.getById(id);
    }
}
