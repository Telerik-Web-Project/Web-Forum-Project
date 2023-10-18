package com.example.webproject.helpers;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.User;
import com.example.webproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class AuthenticationHelper {
    private final UserService userService;
    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }
    public static final String INVALID_AUTHENTICATION_MESSAGE = "Invalid authentication";
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
}
