package com.example.webproject.controllers;
import com.example.webproject.dtos.UpdateUserDto;
import com.example.webproject.dtos.UserDto;
import com.example.webproject.dtos.UserFilterDto;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.UserMapper;
import com.example.webproject.models.User;
import com.example.webproject.models.UserFilter;
import com.example.webproject.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserMvcController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String showAllUser(@ModelAttribute("userFilterOptions") UserFilterDto userFilterDto, Model model) {
        UserFilter userFilter = new UserFilter(
                userFilterDto.getFirstName(),
                userFilterDto.getUsername(),
                userFilterDto.getEmail(),
                userFilterDto.getSortBy(),
                userFilterDto.getSortOrder()
        );
        List<User> users = userService.getAll(userFilter);
        model.addAttribute("userFilterOptions", userFilter);
        model.addAttribute("users", users);
        return "UsersView";
    }

    @GetMapping("{id}")
    public String getUserById(@PathVariable int id, Model model) {
        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode",
                    HttpStatus.NOT_FOUND.getReasonPhrase());

            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/new")
    public String userCreateView(Model model) {
        model.addAttribute("user", new UserDto());
        return "UserCreateView";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("user") UserDto userDto,BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "UserCreateView";
        }
      try {
          User user = userMapper.fromDtoToUser(userDto);
          userService.createUser(user);
          return "HomeView";
      }catch (EntityDuplicateException e){
          bindingResult.rejectValue("username", "duplicate_username", e.getMessage());
          bindingResult.rejectValue("email", "duplicate_email", e.getMessage());
      return "UserCreateView";
      }
    }

    @GetMapping("/update/{id}")
    public String showEditView(@PathVariable int id,Model model) {
        try {
            User user = userService.getById(id);
            UpdateUserDto updateUserDto = userMapper.fromUserToDto(user);

            model.addAttribute("userId", id);
            model.addAttribute("user", updateUserDto);
            return "UserEditView";
        }
        catch (EntityNotFoundException e){
            model.addAttribute("error",e.getMessage());
            return "ErrorView";
        }
    }

}