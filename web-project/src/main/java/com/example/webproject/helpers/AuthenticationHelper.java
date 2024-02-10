package com.example.webproject.helpers;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.User;
import com.example.webproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class AuthenticationHelper {
    private static final String GUEST_USER_KEY = "guestUser";
    private final UserService userService;

    private final Map<String,User> cache = new HashMap<>();
    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
        cache.put(GUEST_USER_KEY,new User());

    }
    public static final String INVALID_AUTHENTICATION_MESSAGE = "Wrong username or password.";
    public static final String VALID_HEADER_NAME = "Authorization";
    public static final int ASCII_VALUE_OF_SPACE = 32;

    public User getUser(HttpHeaders headers){
        if(!headers.containsKey(VALID_HEADER_NAME)){
            throw new AuthorizationException(INVALID_AUTHENTICATION_MESSAGE);
        }
        try {
            String input = headers.getFirst(VALID_HEADER_NAME);
            String username = getUsername(Objects.requireNonNull(input));
            String password = input.substring(username.length() + 1);
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_MESSAGE);
            } else return user;
        }catch (EntityNotFoundException e){
            throw new AuthorizationException(INVALID_AUTHENTICATION_MESSAGE);
        }

    }
    private String getUsername(String input){
        if(input.charAt(0) == ASCII_VALUE_OF_SPACE){
            return "";
        }
        return input.charAt(0) + getUsername(input.substring(1));
    }
    public User tryPopulateUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");
        if(currentUsername == null){
          return cache.get(GUEST_USER_KEY);
        }
        if(cache.containsKey(currentUsername)){

            return cache.get(currentUsername);
        }

        User user = userService.getByUsername(currentUsername);

        cache.put(user.getUsername(),user);

        return user;


    }

    public User tryGetCurrentUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");

        if (currentUsername == null) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_MESSAGE);
        }

        return userService.getByUsername(currentUsername);
    }

    public User tryGetUserDemo(Authentication authentication) {
        String username;
        try {
            username = authentication.getName();
        } catch (NullPointerException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_MESSAGE);
        }
        return userService.getByUsername(username);
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.isAuthenticated();
    }

}
