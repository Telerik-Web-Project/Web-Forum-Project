package com.example.webproject.services;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.User;
import com.example.webproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void updateUser(User user,User userToBeUpdated) {
        checkPermission(user, userToBeUpdated, "Only admins can update other users details");
        userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public void deleteUser(User user,User userToBeDeleted) {
        checkPermission(user, userToBeDeleted, "Only admins can delete other users");
        userRepository.deleteUser(userToBeDeleted);
    }

    private static void checkPermission(User user, User userToBeDeleted, String message) {
        if (!user.isAdmin() && !user.getUsername().equals(userToBeDeleted.getUsername())) {
            throw new AuthorizationException(message);
        }
    }
}
