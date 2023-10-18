package com.example.webproject.helpers;

import com.example.webproject.models.User;
import com.example.webproject.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserMapper() {
    }
    public User fromDtoToUser(UserDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        return user;
    }

}
