package com.example.webproject.controllers.mvc;

import com.example.webproject.helpers.AuthenticationHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class AboutMvcController {
    private final AuthenticationHelper authenticationHelper;
    @Autowired
    public AboutMvcController(AuthenticationHelper authenticationHelper){
        this.authenticationHelper = authenticationHelper;
    }
    @GetMapping("/about")
    public String showAboutPage() {
        return "About";
    }

    @GetMapping("/contacts")
    public String showContactsPage() {
        return "Contacts";
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated() {
        return authenticationHelper.isAuthenticated();
    }
}
