package com.example.webproject.controllers.mvc;
import com.example.webproject.dtos.PostFilterDto;
import com.example.webproject.dtos.UpdateUserDto;
import com.example.webproject.dtos.UserDto;
import com.example.webproject.dtos.UserFilterDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.helpers.UserMapper;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostFilter;
import com.example.webproject.models.User;
import com.example.webproject.models.UserFilter;
import com.example.webproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
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
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserMvcController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {

        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String getPaginationPage(@RequestParam(value = "page", required = false) Integer page,
                                    Model model,
                                    @Valid @ModelAttribute("postFilter") UserFilterDto filterDto) {
        UserFilter userFilter = new UserFilter(filterDto.getFirstName(),
                filterDto.getUsername(),
                filterDto.getEmail(),
                filterDto.getSortBy(),
                filterDto.getSortOrder());
        if(page == null){
            page = 1;
        }
        int itemsPerPage = 5;

        List<User> dataList = userService.getPaginatedPosts(page, itemsPerPage);

        int totalItems = userService.getAll(userFilter).size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        model.addAttribute("users", dataList);
        model.addAttribute("userService", userService);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("userFilter", filterDto);
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
        return "RegisterFormView";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userDto") UserDto userDto,BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "ErrorView";
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

    @GetMapping("/{id}/update")
    public String showEditView(@PathVariable int id,Model model,HttpSession session) {
        try{
            authenticationHelper.tryGetCurrentUser(session);
        }
        catch (AuthorizationException e){
            return "redirect:/auth/login";
        }

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
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id , Model model,HttpSession session) {
       User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetCurrentUser(session);
        }catch (AuthorizationException e){
            return "redirect:/auth/login";
        }
        try{
            User userToBeDeleted = userService.getById(id);
            userService.deleteUser(loggedUser,userToBeDeleted);
            return "redirect:/HomeView";
        }
        catch (EntityNotFoundException e){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
        catch (AuthorizationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        }
        @GetMapping("/{id}/posts")
        public String getUsersPosts(@PathVariable int id,HttpSession session,Model model) {
            User loggedUser;
            try {
                loggedUser = authenticationHelper.tryGetCurrentUser(session);
            } catch (AuthorizationException e) {
                return "redirect:/auth/login";
            }
            try {
                User userToCheckPosts = userService.getById(id);
                model.addAttribute("userPosts", userService.getUserPosts(loggedUser, userToCheckPosts));
                return "UserPostsView";
            } catch (EntityNotFoundException e) {
                model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
                model.addAttribute("error", e.getMessage());
                return "ErrorView";
            }
        }
    }


