package com.example.webproject.services;

import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.*;
import com.example.webproject.repositories.contracts.PhoneRepository;
import com.example.webproject.repositories.contracts.UserRepository;
import com.example.webproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.webproject.helpers.ValidationHelper.*;

@Service
public class UserServiceImpl implements UserService {
    public static final int DATA_BASE_USER_ID = 1;
    private static final String ADMIN_USER_ERR_MESSAGE = "Only admins can delete other users";
    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PhoneRepository phoneRepository) {
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
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
        checkIfBanned(loggedUser);
        return userRepository.getUserPosts(user);
    }

    @Override
    public List<User> getPaginatedUsers(int page, int postsPerPage, UserFilter userFilter) {
        return userRepository.getPaginatedUsers(page, postsPerPage, userFilter);
    }

    @Override
    public int getUsersCount() {
        return userRepository.getUsersCount();
    }

    @Override
    public void createUser(User user) {
        ValidationHelper.validateUserDetails(userRepository, user);
        userRepository.createUser(user);
    }

    @Override
    public User updateUser(User loggedUser, User userToBeUpdated) {
        ValidationHelper.validateUpdatePermission(userToBeUpdated.getId(), loggedUser);
        ValidationHelper.checkIfBanned(loggedUser);
        ValidationHelper.validateEmail(userRepository, userToBeUpdated);
        updateUserDetails(loggedUser, userToBeUpdated);
        userRepository.updateUser(loggedUser);
        return loggedUser;
    }

    @Override
    public void deleteUser(User loggedUser, User userToBeDeleted) {
        ValidationHelper.masterUserAccessDenied(userToBeDeleted.getId());
        ValidationHelper. validateDeletePermission(loggedUser, userToBeDeleted, ADMIN_USER_ERR_MESSAGE);
        userRepository.deleteUser(userToBeDeleted);
    }

    @Override
    public void addPhoneNumber(Phone phone) {
        checkIfBanned(phone.getAdminUser());
        ValidationHelper.validatePhoneIsUnique(phoneRepository,phone);
        ValidationHelper.validateUserHasNoExistingPhone(phoneRepository, phone);
        phoneRepository.createPhone(phone);

    }

    @Override
    public void updatePhoneNumber(User user, Phone phone) {
        checkIfBanned(user);
        validatePhoneIsUnique(phoneRepository, phone);
        Phone oldPhone = phoneRepository.findPhone(user);
        oldPhone.setPhoneNumber(phone.getPhoneNumber());
        phoneRepository.updatePhone(oldPhone);
    }

    @Override
    public void deletePhoneNumber(User user) {
        Phone phone = phoneRepository.findPhone(user);
        phoneRepository.deletePhone(phone);
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

    private void updateUserDetails(User loggedUser, User userToBeUpdated) {
        loggedUser.setFirstName(userToBeUpdated.getFirstName());
        loggedUser.setLastName(userToBeUpdated.getLastName());
        loggedUser.setEmail(userToBeUpdated.getEmail());
        loggedUser.setPassword(userToBeUpdated.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getByUsername(username);
    }
}
