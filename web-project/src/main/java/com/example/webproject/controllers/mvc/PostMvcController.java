package com.example.webproject.controllers.mvc;

import com.example.webproject.dtos.CommentDto;
import com.example.webproject.dtos.PostDto;
import com.example.webproject.dtos.PostFilterDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.helpers.CommentMapper;
import com.example.webproject.helpers.PostMapper;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostFilter;
import com.example.webproject.models.User;
import com.example.webproject.repositories.PostRepositoryImpl;
import com.example.webproject.services.contracts.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/posts")
public class PostMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final PostService postService;
    private final CommentMapper commentMapper;

    @Autowired
    public PostMvcController(AuthenticationHelper authenticationHelper,
                             PostMapper postMapper,
                             PostService postService,
                             CommentMapper commentMapper) {
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.postService = postService;
        this.commentMapper = commentMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {

        return session.getAttribute("currentUser") != null;
    }

//    @GetMapping
//    public String getAll(@Valid @ModelAttribute("postFilter") PostFilterDto filterDto, Model model) {
//        PostFilter postFilter = new PostFilter(
//                filterDto.getTitle(),
//                filterDto.getContent(),
//                filterDto.getSortBy(),
//                filterDto.getSortOrder()
//        );
//        model.addAttribute("postService", postService);
//        model.addAttribute("posts", postService.getAll(postFilter));
//        model.addAttribute("postFilter", filterDto);
//        return "PostsView";
//    }

//    @GetMapping
//    public String getPaginatedPosts(
//            @RequestParam(name = "page")
//            int page, Model model) {
//        model.addAttribute("postService", postService);
//        model.addAttribute("posts",postService.getPaginatedPosts(pageParameterAssignment(page)));
//        return "PostsView";
//    }
//    private static int pageParameterAssignment(int page) {
//        if(page == 0){
//            page = 1;
//        }
//        return page;
//    }

    @GetMapping
    public String getPaginationPage(@RequestParam("page") int page,
                                    Model model,
                                    @Valid @ModelAttribute("postFilter") PostFilterDto filterDto) {
        PostFilter postFilter = new PostFilter(
                filterDto.getTitle(),
                filterDto.getContent(),
                filterDto.getSortBy(),
                filterDto.getSortOrder()
        );
        int itemsPerPage = 5;

        List<Post> dataList = postService.getPaginatedPosts(page, itemsPerPage);

        int totalItems = postService.getAll(postFilter).size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        model.addAttribute("posts", dataList);
        model.addAttribute("postService", postService);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("postFilter", filterDto);
        return "PostsView";
    }


//    @GetMapping("/mostCommented")
//    public String getTenMostCommentedPosts(Model model) {
//        List<Post> posts = postService.getTenMostCommentedPosts();
//        model.addAttribute("posts", posts);
//        return "TenMostCommentedPostsView";
//    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable int id, Model model) {
        try {
            Post post = postService.get(id);
            model.addAttribute("post", post);
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        }
    }

    @GetMapping("/tags")
    public String getPostsWithTag(@Valid @ModelAttribute("filterOptions") PostFilterDto filterDto,
                                 @ModelAttribute("tag") String tag,
                                  Model model) {
        PostFilter postFilter = new PostFilter(
                filterDto.getTitle(),
                filterDto.getContent(),
                filterDto.getSortBy(),
                filterDto.getSortOrder()
        );
        model.addAttribute("posts", postService.getPostsWithTag(tag));
        model.addAttribute("filterOptions", postService.getAll(postFilter));
        return "AllPostsView";
    }

    @GetMapping("/create")
    public String showCreatePostView(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "Not-Found";
        }
        model.addAttribute("post", new PostDto());
        return "Post-New";
    }

    @PostMapping()
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto,
                             BindingResult errors,
                             Model model,
                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        if (errors.hasErrors()) {
            return "Post-New";
        }

        try {
            Post postToCreate = postMapper.fromDto(postDto);
            postService.createPost(postToCreate, user);
            return "redirect:/posts";
        } catch (EntityDuplicateException | UserBannedException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 409);
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.deletePost(postService.get(id), user);
            return "redirect:/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        } catch (UserBannedException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 409);
            return "ErrorView";
        }
    }


    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.addComment(user, postService.get(id), commentMapper.fromDto(new CommentDto()));
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        } catch (UserBannedException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 409);
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        }
        return "redirect:/posts/{id}";
    }
}
