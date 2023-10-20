package com.example.webproject.helpers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.Phone;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.PostRepository;
import com.example.webproject.repositories.UserRepository;
import com.example.webproject.services.PostServiceImpl;
import com.example.webproject.services.UserServiceImpl;


public class ValidationHelper {
    public ValidationHelper() {
    }

    public static void validateEmail(UserRepository userRepository, User userToBeUpdated) {
        boolean emailExists = true;
        try {
            userRepository.getByEmail(userToBeUpdated.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new EntityDuplicateException("User", "email", userToBeUpdated.getEmail());
        }
    }
    public static void checkPermission(User user, User userToBeDeleted, String message) {
        if (!user.isAdmin() && !user.getUsername().equals(userToBeDeleted.getUsername())) {
            throw new AuthorizationException(message);
        }
    }
    public static void checkIfBanned(User loggedUser) {
        if (loggedUser.isBlocked()) {
            throw new UserBannedException();
        }
    }
    public static void validatePhone(UserRepository userRepository,Phone phone) {
        boolean phoneExists = true;
        try {
            userRepository.findPhone(phone.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            phoneExists = false;
        }

        if (phoneExists) {
            throw new EntityDuplicateException("User", "phone number", phone.getPhoneNumber());
        }
    }
    public static void validateUserDetails(UserRepository userRepository,User user) {
        boolean usernameExists = true;
        boolean emailExists = true;
        try {
            userRepository.getByUsername(user.getUsername());
        } catch (EntityNotFoundException e) {
            usernameExists = false;
        }
        try {
            userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        } else if (usernameExists) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }
    }
    public static void validatePostExists(PostRepository postRepository, Post post) {
        Post existingPost = postRepository.get(post.getId());
        if(existingPost.getId() != post.getId()) {
            throw new EntityNotFoundException("Post", "id", String.valueOf(post.getId()));
        }
    }
      public static void validateModifyPermissions(PostRepository postRepository, Post post, User user) {
        Post postToUpdate = postRepository.get(post.getId());
        if (!(user.isAdmin() || postToUpdate.getPostCreator().equals(user))) {
            throw new AuthorizationException(PostServiceImpl.AUTHENTICATION_ERROR);
        }
    }
    public static void masterUserAccessDenied(int id) {
        if(id == UserServiceImpl.DATA_BASE_USER_ID){
            throw new AuthorizationException("Access denied for master user !");
        }
    }
    public static void validateUpdatePermission(int id, User user) {
        if (user.getId() != id) {
            throw new AuthorizationException("You can modify only your personal details");
        }
    }
}
