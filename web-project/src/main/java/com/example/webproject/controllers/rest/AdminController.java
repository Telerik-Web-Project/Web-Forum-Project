package com.example.webproject.controllers.rest;

import com.example.webproject.dtos.PhoneDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.mappers.PhoneMapper;
import com.example.webproject.models.*;
import com.example.webproject.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.example.webproject.helpers.ValidationHelper.validateUserIsAdmin;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final PhoneMapper phoneMapper;
    @Autowired
    public AdminController(UserService userService, AuthenticationHelper authenticationHelper, PhoneMapper phoneMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.phoneMapper = phoneMapper;
    }

    @PutMapping("/ban/{id}")
    public void assignIsBanned(@RequestHeader HttpHeaders httpHeaders, @PathVariable int id) {
        try {
            User userToBan = userService.getById(id);
            validateUserIsAdmin(authenticationHelper.getUser(httpHeaders));
            userService.changeBanStatus(userToBan);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PutMapping("/makeAdmin/{id}")
    public void assignIsAdmin(@RequestHeader HttpHeaders httpHeaders, @PathVariable int id) {
        try {
            User user = userService.getById(id);
            validateUserIsAdmin(authenticationHelper.getUser(httpHeaders));
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
            Phone phone = phoneMapper.fromDto(phoneDto,user);
            validateUserIsAdmin(user);
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
            Phone phone = phoneMapper.fromDto(phoneDto,user);
            userService.updatePhoneNumber(user, phone);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    @DeleteMapping("/phoneNumber")
    public void deletePhone(@RequestHeader HttpHeaders httpHeaders) {
        try {
            User user = authenticationHelper.getUser(httpHeaders);
            userService.deletePhoneNumber(user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


}
