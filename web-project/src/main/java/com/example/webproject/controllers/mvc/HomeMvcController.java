package com.example.webproject.controllers.mvc;

import com.example.webproject.helpers.AuthenticationHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public HomeMvcController(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {

        return session.getAttribute("currentUser") != null;
    }

    @GetMapping()
    public String showHomePage() {
        return "HomeView";
    }

//    @GetMapping
//    public String showMostCommentedPosts(){
//        return "PostsView";
//    }
//
//    @GetMapping
//    public String showMostRecentPosts(){
//        return "PostsView";
//    }

//    @GetMapping("/admin")
//    public String showAdminPortal(HttpSession session, Model model) {
//        try {
//            User user = authenticationHelper.tryGetCurrentUser(session);
//            if(user.isAdmin()){
//                return "AdminPortalView";
//            }
//            return "AccessDeniedView";
//        } catch (AuthorizationException e) {
//            return "redirect:/auth/login";
//        }
//    }
}
