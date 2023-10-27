package com.example.webproject.services;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.models.Phone;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.CommentRepository;
import com.example.webproject.repositories.contracts.PhoneRepository;
import com.example.webproject.repositories.contracts.PostRepository;
import com.example.webproject.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.webproject.Helpers.createMockPhone;
import static com.example.webproject.Helpers.createMockUser;
import static com.example.webproject.helpers.ValidationHelper.*;
import static com.example.webproject.helpers.ValidationHelper.validatePhone;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PhoneRepository phoneRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getById_Should_Return_User_WhenMatchExists(){
        User user = createMockUser();

        Mockito.when(userRepository.getById(Mockito.anyInt()))
                .thenReturn(user);

        User testUser = userService.getById(user.getId());

        Assertions.assertEquals(user,testUser);
    }

    @Test
    public void getById_Should_Throw_WhenNoMatch(){
        User user = createMockUser();
        Mockito.when(userRepository.getById(user.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,()->userService.getById(user.getId()));

        Mockito.verify(userRepository,Mockito.times(1)).getById(user.getId());
    }

    @Test
    public void create_Not_Call_Repository_When_Username_Exists() {
        User user = createMockUser();

        Mockito.when(userRepository.getByUsername(user.getUsername()))
                .thenReturn(user);
        Mockito.when(userRepository.getByEmail(user.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityDuplicateException.class,
                () -> userService.createUser(user));

        Mockito.verify(userRepository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Not_Call_Repository_When_Email_Exists() {
        User user = createMockUser();

        Mockito.when(userRepository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(userRepository.getByEmail(user.getEmail()))
                .thenReturn(user);

        assertThrows(EntityDuplicateException.class,
                () -> userService.createUser(user));

        Mockito.verify(userRepository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Should_Call_Repository_When_PassedValidDetails() {
        User user = createMockUser();

        Mockito.when(userRepository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(userRepository.getByEmail(user.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        userService.createUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).createUser(user);
    }
    @Test
    public void deleteUser_Should_Throw_When_NonAdmin_User_Tries_To_Delete_Other_User() {
        User mockUser = createMockUser();

        User userToBeDeleted = createMockUser();

        userToBeDeleted.setUsername("otherUsername");

        Assertions.assertThrows(AuthorizationException.class,
                ()->userService.deleteUser(mockUser,userToBeDeleted));

    }
    @Test
    public void deleteUser_Should_Call_Repository_When_NonAdmin_User_Deletes_Themselves() {
        User mockUser = createMockUser();

        User userToBeDeleted = createMockUser();

        userService.deleteUser(mockUser,userToBeDeleted);

        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(userToBeDeleted);
    }
    @Test
    public void deleteUser_Should_Call_Repository_When_Admin_User_Delete_Other_User(){
        User adminUser = createMockUser();
        adminUser.setAdmin(true);
        User otherUser = createMockUser();
        otherUser.setUsername("otherUser");

        userService.deleteUser(adminUser,otherUser);

        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(otherUser);
    }

    @Test
    public void get_User_Posts_Should_Call_Repository_IfLoggedUser_Is_Not_Banned(){
        User loggedUser = createMockUser();

        userService.getUserPosts(loggedUser, userRepository.getById(Mockito.anyInt()));

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserPosts(userRepository.getById(Mockito.anyInt()));
    }
    @Test
    public void get_User_Posts_Should_Throw_When_LoggedUser_Is_Banned(){
        User loggedUser = createMockUser();
        loggedUser.setBlocked(true);
        User userToCheckPosts = createMockUser();

        Assertions.assertThrows(UserBannedException.class,
                ()->userService.getUserPosts(loggedUser,userToCheckPosts));

        Mockito.verify(userRepository, Mockito.times(0))
                .getUserPosts(userRepository.getById(Mockito.anyInt()));
    }
    @Test
    public void change_Ban_Status_Should_Ban_User(){
        User mockUser = createMockUser();

        userService.changeBanStatus(mockUser);

        Assertions.assertTrue(mockUser.isBlocked());
    }
    @Test
    public void change_Admin_Status_Should_Make_User_Admin(){
        User mockUser = createMockUser();

        userService.changeAdminStatus(mockUser);

        Assertions.assertTrue(mockUser.isAdmin());
    }
    @Test
    public void add_Phone_Should_Throw_When_Non_Admin_User_Tries_To_Add_Phone(){
        User user = createMockUser();

        Phone phone = createMockPhone();

        phone.setAdminUser(user);

        Assertions.assertThrows(AuthorizationException.class,()->userService.addPhoneNumber(phone));

        Mockito.verify(phoneRepository,Mockito.times(0)).createPhone(phone);
    }
    @Test
    public void add_Phone_Should_Throw_When_Banned_User_Tries_To_Add_Phone(){
        User user = createMockUser();

        user.setAdmin(true);

        user.setBlocked(true);

        Phone phone = createMockPhone();

        phone.setAdminUser(user);

        Assertions.assertThrows(UserBannedException.class,()->userService.addPhoneNumber(phone));
    }
    @Test
    public void add_Phone_Should_Throw_If_Duplicate_Number(){
        User user = createMockUser();

        user.setAdmin(true);

        Phone phone = createMockPhone();

        phone.setAdminUser(user);

        Mockito.when(phoneRepository.
                findPhone(phone.getAdminUser())).thenThrow(EntityNotFoundException.class);

        Mockito.when(phoneRepository.
                findPhone(phone.getPhoneNumber())).thenReturn(phone);

        Assertions.assertThrows(EntityDuplicateException.class,()->userService.addPhoneNumber(phone));
    }
    @Test
    public void add_Phone_Should_Throw_If_User_Already_Has_Phone(){
        User user = createMockUser();

        user.setAdmin(true);

        Phone phone = createMockPhone();

        phone.setAdminUser(user);

        Mockito.when(phoneRepository.
                findPhone(Mockito.any(User.class))).thenReturn(phone);


        Assertions.assertThrows(AuthorizationException.class,()->userService.addPhoneNumber(phone));
    }


    @Test
    public void add_Phone_Should_Call_Repository_When_User_Is_Admin_And_Passed_Valid_Phone(){
       User user = createMockUser();
       user.setAdmin(true);
       Phone phone = createMockPhone();
       phone.setAdminUser(user);

       Mockito.when(phoneRepository.findPhone(Mockito.anyString()))
               .thenThrow(EntityNotFoundException.class);
        Mockito.when(phoneRepository.findPhone(Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

       userService.addPhoneNumber(phone);

       Mockito.verify(phoneRepository,Mockito.times(1)).createPhone(phone);

    }
    @Test
    public void delete_Phone_Should_Throw_When_User_is_Banned(){
        User user = createMockUser();

        user.setBlocked(true);

        Assertions.assertThrows(UserBannedException.class,()->userService.deletePhoneNumber(user));
    }
    @Test
    public void delete_Should_Not_Call_Repository_When_User_Dont_Have_Phone(){
        User user = createMockUser();


        Phone phone = createMockPhone();

        userService.deletePhoneNumber(user);

        Mockito.verify(phoneRepository,Mockito.times(0)).deletePhone(phone);
    }
    @Test
    public void delete_Should_Call_Repository_When_Valid_Arguments_Passed(){
        User user = createMockUser();

        Phone phone = createMockPhone();
        phone.setAdminUser(user);

        Mockito.when(phoneRepository.findPhone(Mockito.any(User.class))).thenReturn(phone);

        userService.deletePhoneNumber(user);

        Mockito.verify(phoneRepository,Mockito.times(1)).deletePhone(phone);
    }
    @Test
    public void update_User_Should_Call_Repository_When_Valid_Arguments_Passed(){
        User loggedUser = createMockUser();
        User userToBeUpdated = createMockUser();
        userToBeUpdated.setFirstName("mockName");
        userToBeUpdated.setEmail("mockTestEmail");

        Mockito.when(userRepository.getByEmail(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        userService.updateUser(loggedUser,userToBeUpdated);

        Mockito.verify(userRepository,Mockito.times(1)).updateUser(loggedUser);
    }
    @Test
    public void update_User_Should_Throw_When_User_Is_Banned(){
        User loggedUser = createMockUser();
        User userToBeUpdated = createMockUser();

        userToBeUpdated.setFirstName("mockName");
        userToBeUpdated.setEmail("mockTestEmail");

        loggedUser.setBlocked(true);

        Assertions.assertThrows(UserBannedException.class,
                ()->userService.updateUser(loggedUser,userToBeUpdated));
    }
    @Test
    public void update_Phone_Should_Throw_When_User_IsBanned(){
        User user = createMockUser();
        user.setBlocked(true);

        Phone phone = createMockPhone();
        phone.setAdminUser(user);

        Assertions.assertThrows(UserBannedException.class,
                ()->userService.updatePhoneNumber(user,phone));
    }
    @Test
    public void update_Phone_Should_Throw_When_Duplicate_Phone_Number(){
        User user = createMockUser();

        Phone phone = createMockPhone();

        phone.setAdminUser(user);

        Mockito.when(phoneRepository.findPhone(Mockito.any(String.class)))
                .thenReturn(phone);
        Assertions.assertThrows(EntityDuplicateException.
                class,()->userService.updatePhoneNumber(user,phone));

    }
}

