package com.example.webproject.services;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
      }catch (EntityNotFoundException e){
          usernameExists = false;
      }
      try {
          userRepository.getByEmail(user.getEmail());
      } catch (EntityNotFoundException e){
          emailExists = false;
      }
      if(emailExists){
          throw new EntityDuplicateException("User","email",user.getEmail());}
      else if (usernameExists){
          throw new EntityDuplicateException("User","username",user.getUsername());
      }
       userRepository.createUser(user);
    }
    @Override
    public List<Post> getUserPosts(User loggedUser,User user) {
        checkIfBanned(loggedUser);
        return userRepository.getUserPosts(user);
    }


    @Override
    public User updateUser(int id, User loggedUser, User userToBeUpdated) {
        mapToUser(id, loggedUser, userToBeUpdated);
        userRepository.updateUser(loggedUser);
        return loggedUser;
    }


    @Override
    public void deleteUser(User loggedUser, User userToBeDeleted) {
        checkPermission(loggedUser, userToBeDeleted, "Only admins can delete other users");
        userRepository.deleteUser(userToBeDeleted);
    }

    @Override
    public void changeBanStatus(User user) {
        if(user.isBlocked()){
            user.setBlocked(false);
        }
        else{
            user.setBlocked(true);
        }
        userRepository.updateUser(user);
    }

    private static void checkPermission(User user, User userToBeDeleted, String message) {
        if (!user.isAdmin() && !user.getUsername().equals(userToBeDeleted.getUsername())) {
            throw new AuthorizationException(message);
        }
    }


    private static void checkIfBanned(User loggedUser) {
        if(loggedUser.isBlocked()){
            throw new UserBannedException();
        }
    }
    private static void mapToUser(int id, User loggedUser, User userToBeUpdated) {
        loggedUser.setId(id);
        loggedUser.setFirstName(userToBeUpdated.getFirstName());
        loggedUser.setLastName(userToBeUpdated.getLastName());
        loggedUser.setEmail(userToBeUpdated.getEmail());
        loggedUser.setPassword(userToBeUpdated.getPassword());
    }
}
