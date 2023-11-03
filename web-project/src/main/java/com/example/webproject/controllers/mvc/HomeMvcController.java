package com.example.webproject.controllers.mvc;

import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.services.contracts.PostService;
import com.example.webproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final PostService postService;
    @Autowired
    public HomeMvcController(AuthenticationHelper authenticationHelper, UserService userService, PostService postService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.postService = postService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {

        return session.getAttribute("currentUser") != null;
    }

    @GetMapping()
    public String showHomePage(Model model) {
        int activeUsers = userService.getUsersCount();
        List<Post> topPosts = postService.getTenMostCommentedPosts();
        model.addAttribute("usersCount",activeUsers);
        model.addAttribute("topPosts",topPosts);
        return "HomeView";
    }

    @GetMapping("/top/posts")
    public String showMostCommentedPosts(){
        return "PostsView";
    }

    @GetMapping("/recent/posts")
    public String showMostRecentPosts(){
        return "PostsView";
    }

    @GetMapping("/admin")
    public String showAdminPortal(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            if(user.isAdmin()){
                return "AdminPortalView";
            }
            return "AccessDeniedView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }
}
