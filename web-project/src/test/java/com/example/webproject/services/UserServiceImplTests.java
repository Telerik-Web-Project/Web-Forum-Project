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

        assertThrows(EntityDuplicateException.class, () -> {
            userService.createUser(user);
        });

        Mockito.verify(repository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Should_Throw_When_EmailExists() {
        User user = createMockUser();

        Mockito.when(repository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.getByEmail(user.getEmail()))
                .thenReturn(user);

        assertThrows(EntityDuplicateException.class, () -> {
            userService.createUser(user);
        });

        Mockito.verify(repository, Mockito.times(0)).createUser(user);
    }
    @Test
    public void create_Should_callRepository_When_PassedValidDetails() {
        User user = createMockUser();

        Mockito.when(repository.getByUsername(user.getUsername()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.getByEmail(user.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        userService.createUser(user);

        Mockito.verify(repository, Mockito.times(1)).createUser(user);
    }
}
