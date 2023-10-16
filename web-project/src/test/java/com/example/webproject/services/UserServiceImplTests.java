package com.example.webproject.services;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.User;
import com.example.webproject.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.example.webproject.Helpers.createMockUser;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getById_Should_Return_User_WhenMatchExists(){
        User user = createMockUser();

        Mockito.when(repository.getById(Mockito.anyInt()))
                .thenReturn(user);

        User methodUser = userService.getById(user.getId());

        Assertions.assertEquals(user,methodUser);
    }

    @Test
    public void getById_Should_Throw_WhenNoMatch(){
        User user = createMockUser();
        Mockito.when(repository.getById(user.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,()->userService.getById(user.getId()));

        Mockito.verify(repository,Mockito.times(1)).getById(user.getId());
    }

    @Test
    public void create_Should_Throw_When_UsernameExists() {
        User user = createMockUser();

        Mockito.when(repository.getByUsername(user.getUsername()))
                .thenReturn(user);
        Mockito.when(repository.getByEmail(user.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityDuplicateException.class,
                () -> userService.createUser(user));

        Mockito.verify(repository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Should_Throw_When_EmailExists() {
        User user = createMockUser();

        Mockito.when(repository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.getByEmail(user.getEmail()))
                .thenReturn(user);

        assertThrows(EntityDuplicateException.class,
                () -> userService.createUser(user));

        Mockito.verify(repository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Should_Call_Repository_When_PassedValidDetails() {
        User user = createMockUser();

        Mockito.when(repository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.getByEmail(user.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        userService.createUser(user);

        Mockito.verify(repository, Mockito.times(1)).createUser(user);
    }
    @Test
    public void deleteUser_Should_Not_Call_Repository_When_NonAdmin_User_Tries_To_Delete_Other_User() {
        User mockUser = createMockUser();
        mockUser.setAdmin(false);

        User userToBeDeleted = createMockUser();
        userToBeDeleted.setUsername("otherUsername");

        Mockito.verify(repository, Mockito.times(0)).deleteUser(userToBeDeleted);
    }
    @Test
    public void deleteUser_Should_Call_Repository_When_NonAdmin_User_Deletes_Themselves() {
        User mockUser = createMockUser();
        mockUser.setAdmin(false);

        User userToBeDeleted = createMockUser();

        userService.deleteUser(mockUser,userToBeDeleted);

        Mockito.verify(repository, Mockito.times(1)).deleteUser(userToBeDeleted);
    }
    @Test
    public void deleteUser_Should_Call_Repository_When_Admin_User_Delete_Other_User(){
        User adminUser = createMockUser();
        adminUser.setAdmin(true);
        User otherUser = createMockUser();
        otherUser.setUsername("otherUser");

        userService.deleteUser(adminUser,otherUser);

        Mockito.verify(repository, Mockito.times(1)).deleteUser(otherUser);
    }
    @Test
    public void updateUser_Should_Not_Call_Repository_When_NonAdmin_User_Tries_To_Update_Other_User() {
        User normalUser = createMockUser();
        normalUser.setAdmin(false);

        User userToBeUpdated = createMockUser();
        userToBeUpdated.setUsername("otherUser");

        Mockito.verify(repository, Mockito.times(0)).updateUser(userToBeUpdated);
    }
    @Test
    public void updateUser_Should_Call_Repository_When_Admin_User_Tries_To_Update_Other_User() {
        User adminUser = createMockUser();
        adminUser.setAdmin(true);

        User userToBeUpdated = createMockUser();
        userToBeUpdated.setUsername("otherUser");

        userService.updateUser(adminUser,userToBeUpdated);

        Mockito.verify(repository, Mockito.times(1)).updateUser(userToBeUpdated);
    }
}

