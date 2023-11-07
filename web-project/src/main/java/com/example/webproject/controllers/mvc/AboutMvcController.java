package com.example.webproject.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
}
