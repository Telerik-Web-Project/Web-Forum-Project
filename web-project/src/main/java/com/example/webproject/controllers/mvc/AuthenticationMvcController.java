package com.example.webproject.controllers.mvc;

import com.example.webproject.dtos.LoginDto;
import com.example.webproject.dtos.RegisterDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.mappers.UserMapper;
import com.example.webproject.models.User;
import com.example.webproject.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationMvcController(UserService userService,
                                       AuthenticationHelper authenticationHelper,
                                       UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto login,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }

        try {
            User user = authenticationHelper.verifyAuthentication(login.getUsername(), login.getPassword());
            session.setAttribute("currentUser", login.getUsername());
            session.setAttribute("isAdmin", user.isAdmin());
            return "redirect:/";
        } catch (AuthorizationException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

   @GetMapping("/register")
   public String showRegisterPage(Model model,BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
           return "RegisterFormView";
       }
       model.addAttribute("register", new RegisterDto());
       return "RegisterFormView";
   }

   @PostMapping("/register")
   public String handleRegister(@Valid @ModelAttribute("register") RegisterDto register,
                                BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
           return "RegisterFormView";
       }

       if (!register.getPassword().equals(register.getConfirmPassword())) {
           bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
           return "RegisterFormView";
       }

       try {
           User user = userMapper.fromRegisterDto(register);
           userService.createUser(user);
           return "redirect:/auth/login";
       } catch (EntityDuplicateException e) {
           bindingResult.rejectValue("username", "username_error", e.getMessage());
           bindingResult.rejectValue("email", "email_error", e.getMessage());
           return "RegisterFormView";
       }
   }

}
