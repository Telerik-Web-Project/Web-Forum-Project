package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.*;
import com.example.webproject.repositories.CommentRepository;
import com.example.webproject.repositories.PostRepository;
import com.example.webproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private  final PostRepository postRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<User> getAll(UserFilter userFilter) {
        return userRepository.getAll(userFilter);
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public void createUser(User user) {
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
        userRepository.createUser(user);
    }

    @Override
    public List<Post> getUserPosts(User loggedUser, User user) {
        checkIfBanned(loggedUser);
        return userRepository.getUserPosts(user);
    }


    @Override
    public User updateUser(User loggedUser, User userToBeUpdated) {
        boolean emailExists = true;
        try {
            userRepository.getByEmail(userToBeUpdated.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new EntityDuplicateException("User", "email", userToBeUpdated.getEmail());
        }
        mapToUser(loggedUser, userToBeUpdated);
        userRepository.updateUser(loggedUser);
        return loggedUser;
    }


    @Override
    public void deleteUser(User loggedUser, User userToBeDeleted) {
        checkPermission(loggedUser, userToBeDeleted, "Only admins can delete other users");
        setToDefault(userToBeDeleted);
        userRepository.deleteUser(userToBeDeleted);
    }

    @Override
    public void changeBanStatus(User user) {
        if (user.isBlocked()) {
            user.setBlocked(false);
        } else {
            user.setBlocked(true);
        }
        userRepository.updateUser(user);
    }

    @Override
    public void changeAdminStatus(User user) {
        if (user.isAdmin()) {
            user.setAdmin(false);
        } else {
            user.setAdmin(true);
        }
        userRepository.updateUser(user);
    }

    @Override
    public void addPhoneNumber(Phone phone) {
        checkIfBanned(phone.getAdminUser());

        boolean phoneExists = true;
        try {
            userRepository.findPhone(phone.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            phoneExists = false;
        }

        if (phoneExists) {
            throw new EntityDuplicateException("User", "phone number", phone.getPhoneNumber());
        }

        if (phone.getAdminUser().isAdmin()) {
            userRepository.createPhone(phone);
        } else {
            throw new AuthorizationException("Only admins can add phone numbers");
        }

    }

    private void setToDefault(User userToBeDeleted) {
        List<Comment> userComments = commentRepository.getUserComments(userToBeDeleted);
        User defaultUser = userRepository.getById(Integer.MAX_VALUE);
        for (Comment comment : userComments) {
            comment.setUser(defaultUser);
            commentRepository.updateComment(comment);
        }
        List<Post> userPosts = userRepository.getUserPosts(userToBeDeleted);
        for (Post post : userPosts) {
            post.setPostCreator(defaultUser);
            postRepository.updatePost(post);
        }
    }

    private static void checkPermission(User user, User userToBeDeleted, String message) {
        if (!user.isAdmin() && !user.getUsername().equals(userToBeDeleted.getUsername())) {
            throw new AuthorizationException(message);
        }
    }

    private static void checkIfBanned(User loggedUser) {
        if (loggedUser.isBlocked()) {
            throw new UserBannedException();
        }
    }

    private static void mapToUser(User loggedUser, User userToBeUpdated) {
        loggedUser.setFirstName(userToBeUpdated.getFirstName());
        loggedUser.setLastName(userToBeUpdated.getLastName());
        loggedUser.setEmail(userToBeUpdated.getEmail());
        loggedUser.setPassword(userToBeUpdated.getPassword());
    }
}
