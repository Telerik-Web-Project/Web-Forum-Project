package com.example.webproject.helpers;

import com.example.webproject.dtos.UpdateUserDto;
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

    public User fromUpdateUserDto(UpdateUserDto updateUserDto){
        User user = new User();
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPassword(updateUserDto.getPassword());
        user.setEmail(updateUserDto.getEmail());
        return user;
    } {

    }

}
