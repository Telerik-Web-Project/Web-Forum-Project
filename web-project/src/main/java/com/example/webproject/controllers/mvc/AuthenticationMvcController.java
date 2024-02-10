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
@RequestMapping("/login")
public class AuthenticationMvcController {


    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AuthenticationMvcController(AuthenticationHelper authenticationHelper) {

        this.authenticationHelper = authenticationHelper;

    }
    @GetMapping()
    public String showLoginPage() {
        return "LoginView";
    }
    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated() {
      return authenticationHelper.isAuthenticated();
    }


   }


