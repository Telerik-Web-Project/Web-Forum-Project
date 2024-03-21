package com.example.webproject.mappers;

import com.example.webproject.dtos.RegisterDto;
import com.example.webproject.dtos.UpdateUserDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.models.User;
import com.example.webproject.dtos.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserMapper() {
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
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setEmail(registerDto.getEmail());
        return user;
    }

    public User fromUpdateUserDto(UpdateUserDto updateUserDto){
        User user = new User();
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPassword(updateUserDto.getPassword());
        user.setEmail(updateUserDto.getEmail());
        return user;
    }
    public UpdateUserDto fromUserToDto(User user){
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName(user.getFirstName());
        updateUserDto.setLastName(user.getLastName());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setPassword(user.getPassword());
        return updateUserDto;
    }
    public User fromRegisterDto(RegisterDto registerDto){
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        return user;
    }
}
