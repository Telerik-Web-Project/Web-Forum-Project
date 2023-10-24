package com.example.webproject.helpers;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.Comment;
import com.example.webproject.models.Phone;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.CommentRepository;
import com.example.webproject.repositories.contracts.PhoneRepository;
import com.example.webproject.repositories.contracts.PostRepository;
import com.example.webproject.repositories.contracts.UserRepository;
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
    public static void validatePhone(PhoneRepository phoneRepository, Phone phone) {
        boolean phoneExists = true;
        try {
            phoneRepository.findPhone(phone.getPhoneNumber());
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

    public static void validateCommentExists(CommentRepository commentRepository, Comment comment) {
        Comment existingComment = commentRepository.get(comment.getId());
        if(existingComment.getId() != comment.getId()) {
            throw new EntityNotFoundException("Comment", "id", String.valueOf(comment.getId()));
        }
    }

      public static void validateModifyPermissions(PostRepository postRepository, Post post, User user) {
        Post postToModify = postRepository.get(post.getId());
        if (!(user.isAdmin() || postToModify.getPostCreator().equals(user))) {
            throw new AuthorizationException(PostServiceImpl.AUTHENTICATION_ERROR);
        }
    }

    public static void validateModifyPermissions(CommentRepository commentRepository, Comment comment, User user) {
        Comment commentToModify = commentRepository.get(comment.getId());
        if (!(user.isAdmin() || commentToModify.getUser().equals(user))) {
            throw new AuthorizationException(PostServiceImpl.AUTHENTICATION_ERROR);
        }
    }
    public static void validateUserIsAdmin(PhoneRepository phoneRepository,Phone phone) {
        if (phone.getAdminUser().isAdmin()) {
            phoneRepository.createPhone(phone);
        } else {
            throw new AuthorizationException("Only admins can add phone numbers");
        }
    }
    public static void validateNoOtherPhoneInRepository(PhoneRepository phoneRepository,Phone phone) {
        boolean userHasPhone = true;
        try {
            phoneRepository.findPhone(phone.getAdminUser());
        } catch (EntityNotFoundException e) {
            userHasPhone = false;
        }
        if (userHasPhone) {
            throw new AuthorizationException("You are only allowed to add one phone");
        }
    }

    public static void masterUserAccessDenied(int id) {
        if(id == UserServiceImpl.DATA_BASE_USER_ID){
            throw new AuthorizationException("Access denied for master user !");
            //TODO check if error message if correct
        }
    }
    public static void validateUpdatePermission(int id, User user) {
        if (user.getId() != id) {
            throw new AuthorizationException("You can modify only your personal details");
        }
    }
}
