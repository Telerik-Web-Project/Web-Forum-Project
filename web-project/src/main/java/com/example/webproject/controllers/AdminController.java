package com.example.webproject.controllers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.models.*;
import com.example.webproject.services.UserService;
import jakarta.validation.Valid;
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

    @PutMapping("/ban/{id}")
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

    @PutMapping("/makeAdmin/{id}")
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

    @PostMapping("/phoneNumber")
    public void addPhoneNumber(@RequestHeader HttpHeaders httpHeaders, @Valid @RequestBody PhoneDto phoneDto) {
        try {
            User user = authenticationHelper.getUser(httpHeaders);
            Phone phone = new Phone();
            phone.setPhoneNumber(phoneDto.getPhoneNumber());
            phone.setAdminUser(user);
            userService.addPhoneNumber(phone);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/phoneNumber")
    public void updatePhoneNumber(@RequestHeader HttpHeaders httpHeaders, @Valid @RequestBody PhoneDto phoneDto) {
        try {
            User user = authenticationHelper.getUser(httpHeaders);
            Phone phone = new Phone();
            phone.setPhoneNumber(phoneDto.getPhoneNumber());
            userService.updatePhoneNumber(user, phone);
        }catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/phoneNumber")
    public void deleteUser(@RequestHeader HttpHeaders httpHeaders) {
        try {
            User user = authenticationHelper.getUser(httpHeaders);
            userService.deletePhoneNumber(user);
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
    /* @GetMapping("/{id}")
    public User get(@RequestHeader HttpHeaders httpHeaders, @PathVariable int id) {
        try {
            checkAccessPermissions(authenticationHelper.getUser(httpHeaders));
            return userService.getById(id);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }*/
}
