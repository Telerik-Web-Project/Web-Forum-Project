package com.example.webproject.mappers;

import com.example.webproject.dtos.RegisterDto;
import com.example.webproject.dtos.UpdateUserDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.models.Role;
import com.example.webproject.models.User;
import com.example.webproject.dtos.UserDto;
import com.example.webproject.repositories.contracts.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserMapper {
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    @Autowired
    public UserMapper(PasswordEncoder encoder, RoleRepository roleRepository) {
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }
    public User fromDtoToUser(UserDto userDto){
        User user = fromUpdateUserDto(userDto);
        user.setUsername(userDto.getUsername());
        return user;
    }

    public User fromRegisterDtoToUser(RegisterDto registerDto){
        if(!registerDto.getPassword().equals(registerDto.getConfirmPassword())){
            throw new AuthorizationException("Passwords does not match please try again");
        }

        String encoded = encoder.encode(registerDto.getPassword());

        Role userRole = roleRepository.findByAuthority("USER");

        Set<Role> rolesSet = new HashSet<>();

        rolesSet.add(userRole);

        return new User(registerDto.getFirstName(),
                registerDto.getLastName(),
                registerDto.getUsername(),
                encoded,registerDto.getEmail(),
                rolesSet);
    }

    public User fromUpdateUserDto(UpdateUserDto updateUserDto){
        String encoded =  encoder.encode(updateUserDto.getPassword());

        Role userRole = roleRepository.findByAuthority("USER");

        Set<Role> rolesSet = new HashSet<>();

        rolesSet.add(userRole);

       return new User(updateUserDto.getFirstName(),
                        updateUserDto.getLastName(),
                        encoded,
                        updateUserDto.getEmail(),
                        rolesSet);
    }
    public UpdateUserDto fromUserToDto(User user){
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName(user.getFirstName());
        updateUserDto.setLastName(user.getLastName());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setPassword(user.getPassword());
        return updateUserDto;
    }

}
