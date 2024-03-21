package com.example.webproject.controllers.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class AboutMvcController {

    @GetMapping("/about")
    public String showAboutPage() {
        return "About";
    }

    @GetMapping("/contacts")
    public String showContactsPage() {
        return "Contacts";
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }
}
