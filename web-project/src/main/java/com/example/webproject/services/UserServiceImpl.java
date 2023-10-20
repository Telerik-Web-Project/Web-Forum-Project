package com.example.webproject.services;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.*;
import com.example.webproject.repositories.CommentRepository;
import com.example.webproject.repositories.PostRepository;
import com.example.webproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    public static final int DATA_BASE_USER_ID = 1;
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
        List<User> usersList = userRepository.getAll(userFilter);
        usersList.remove(userRepository.getById(UserServiceImpl.DATA_BASE_USER_ID));
        return usersList;
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
    public List<Post> getUserPosts(User loggedUser, User user) {
        ValidationHelper.checkIfBanned(loggedUser);
       List <Post> userPosts = userRepository.getUserPosts(user);
       if(userPosts.isEmpty()){
           throw new EntityNotFoundException(user.getId());
       }
        return userPosts;
    }
    @Override
    public void createUser(User user) {
        ValidationHelper.validateUserDetails(userRepository,user);
        userRepository.createUser(user);
    }
    @Override
    public User updateUser(User loggedUser, User userToBeUpdated) {
        ValidationHelper.validateEmail(userRepository,userToBeUpdated);
        updateUserDetails(loggedUser, userToBeUpdated);
        userRepository.updateUser(loggedUser);
        return loggedUser;
    }
    @Override
    public void deleteUser(User loggedUser, User userToBeDeleted) {
        ValidationHelper.checkPermission(loggedUser, userToBeDeleted, "Only admins can delete other users");
        saveDeletedUserDataInDataBase(userToBeDeleted);
        userRepository.deleteUser(userToBeDeleted);
    }
    @Override
    public void changeBanStatus(User user) {
        user.setBlocked(!user.isBlocked());
        userRepository.updateUser(user);
    }

    @Override
    public void changeAdminStatus(User user) {
        user.setAdmin(!user.isAdmin());
        userRepository.updateUser(user);
    }

    @Override
    public void addPhoneNumber(Phone phone) {
        ValidationHelper.checkIfBanned(phone.getAdminUser());
        ValidationHelper.validatePhone(userRepository,phone);
        if (phone.getAdminUser().isAdmin()) {
            userRepository.createPhone(phone);
        } else {
            throw new AuthorizationException("Only admins can add phone numbers");
        }
    }

    private void saveDeletedUserDataInDataBase(User userToBeDeleted) {
        List<Comment> userComments = commentRepository.getUserComments(userToBeDeleted);
        User defaultUser = userRepository.getById(DATA_BASE_USER_ID);
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
    private void updateUserDetails(User loggedUser, User userToBeUpdated) {
        loggedUser.setFirstName(userToBeUpdated.getFirstName());
        loggedUser.setLastName(userToBeUpdated.getLastName());
        loggedUser.setEmail(userToBeUpdated.getEmail());
        loggedUser.setPassword(userToBeUpdated.getPassword());
    }
    /*private void validateEmail(User userToBeUpdated) {
        boolean emailExists = true;
        try {
            userRepository.getByEmail(userToBeUpdated.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new EntityDuplicateException("User", "email", userToBeUpdated.getEmail());
        }
    }*/
 /*   private static void checkPermission(User user, User userToBeDeleted, String message) {
        if (!user.isAdmin() && !user.getUsername().equals(userToBeDeleted.getUsername())) {
            throw new AuthorizationException(message);
        }
    }*/


  /*  private void validatePhone(Phone phone) {
        boolean phoneExists = true;
        try {
            userRepository.findPhone(phone.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            phoneExists = false;
        }

        if (phoneExists) {
            throw new EntityDuplicateException("User", "phone number", phone.getPhoneNumber());
        }
    }*/
   /* private void validateUserDetails(User user) {
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
    }*/
}
