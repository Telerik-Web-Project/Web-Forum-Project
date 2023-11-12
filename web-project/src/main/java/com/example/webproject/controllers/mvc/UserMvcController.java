package com.example.webproject.controllers.mvc;
import com.example.webproject.dtos.RegisterDto;
import com.example.webproject.dtos.UpdateUserDto;
import com.example.webproject.dtos.UserFilterDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.mappers.UserMapper;
import com.example.webproject.models.User;
import com.example.webproject.models.UserFilter;
import com.example.webproject.services.contracts.PostService;
import com.example.webproject.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PostService postService;

    @Autowired
    public UserMvcController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper, PostService postService, PostService postService1) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
        this.postService = postService1;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String getPaginationPage(@RequestParam(value = "page", required = false) Integer page,
                                    Model model,
                                    @Valid @ModelAttribute("postFilter") UserFilterDto filterDto, HttpSession session) {
        UserFilter userFilter = new UserFilter(filterDto.getFirstName(),
                filterDto.getUsername(),
                filterDto.getEmail(),
                filterDto.getSortBy(),
                filterDto.getSortOrder());
        if (page == null || page == 0){
            page = 1;
        }
        int itemsPerPage = 5;

        List<User> dataList = userService.getPaginatedUsers(page, itemsPerPage, userFilter);
//        User loggedUser = authenticationHelper.tryGetCurrentUser(session);


        int totalItems = userService.getAll(userFilter).size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            model.addAttribute("users", dataList);
//            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userService", userService);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("userFilter", filterDto);
        return "UsersView";
    }

    @GetMapping("/{id}")
    public String getUserById(HttpSession session, @PathVariable int id, Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetCurrentUser(session);
            User user = userService.getById(id);
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("user", user);
            model.addAttribute("userService", userService);
            model.addAttribute("userPosts", userService.getUserPosts(loggedUser,user));
            model.addAttribute("postService", postService);
            return "SingleUserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode",
                    HttpStatus.NOT_FOUND.getReasonPhrase());

            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/new")
    public String userCreateView(Model model) {
        model.addAttribute("user", new RegisterDto());
        return "RegisterFormView";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("user") RegisterDto registerDto, BindingResult bindingResult,
                             Model model) {
        if(bindingResult.hasErrors()){
            return "RegisterFormView";
        }
      try {
          User user = userMapper.fromRegisterDtoToUser(registerDto);
          userService.createUser(user);
          model.addAttribute("user", registerDto);
          return "redirect:/";
      }catch (EntityDuplicateException e) {
          if (e.getMessage().contains("username")) {
              bindingResult.rejectValue("username", "duplicate_username", e.getMessage());
          } else if (e.getMessage().contains("email")) {
              bindingResult.rejectValue("email", "duplicate_email", e.getMessage());
          }
          return "RegisterFormView";
      }catch (AuthorizationException e){
          model.addAttribute("error",e.getMessage());
          return "ErrorView";
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
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error",e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute @PathVariable int id,
                             @Valid @ModelAttribute("user")UpdateUserDto updateUserDto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "UserEditView";
        }

        try {
            User loggedUser = authenticationHelper.tryGetCurrentUser(session);
            User updatedUser = userMapper.fromUpdateUserDto(updateUserDto);
            updatedUser.setId(loggedUser.getId());
            userService.updateUser(loggedUser, updatedUser);
            model.addAttribute("id",id);
            return "redirect:/users/{id}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("email", "duplicate_email", e.getMessage());
            return "UserEditView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id , Model model,HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            User userToBeDeleted = userService.getById(id);
            userService.deleteUser(loggedUser, userToBeDeleted);
            return "redirect:/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/{id}/makeAdmin")
    public String makeUserAdmin(@PathVariable int id){
        try{
            User user = userService.getById(id);
            userService.changeAdminStatus(user);
        }catch (EntityNotFoundException e){
            return "ErrorView";
        }
        return "redirect:/users/{id}";
    }

    @GetMapping("/{id}/blockUser")
    public String blockUser(@PathVariable int id){
        try{
            User user = userService.getById(id);
            userService.changeBanStatus(user);
        }catch (EntityNotFoundException e){
            return "ErrorView";
        }
        return "redirect:/users/{id}";
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


