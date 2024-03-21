package com.example.webproject.controllers.mvc;

import com.example.webproject.dtos.CommentDto;
import com.example.webproject.dtos.PostDto;
import com.example.webproject.dtos.PostFilterDto;
import com.example.webproject.dtos.UpdatePostDto;
import com.example.webproject.dtos.mvcDtos.SingletonCommentDto;
import com.example.webproject.exceptions.AuthorizationException;
import com.example.webproject.exceptions.EntityDuplicateException;
import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.exceptions.UserBannedException;
import com.example.webproject.helpers.AuthenticationHelper;
import com.example.webproject.mappers.CommentMapper;
import com.example.webproject.mappers.PostMapper;
import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostFilter;
import com.example.webproject.models.User;
import com.example.webproject.services.contracts.CommentService;
import com.example.webproject.services.contracts.PostService;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final int DEFAULT_PAGE_SIZE = 6;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final SingletonCommentDto singletonCommentDto;

    @Autowired
    public PostMvcController(AuthenticationHelper authenticationHelper,
                             PostMapper postMapper,
                             PostService postService,
                             CommentMapper commentMapper, CommentService commentService, SingletonCommentDto singletonCommentDto) {
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.postService = postService;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.singletonCommentDto = singletonCommentDto;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String getPaginationPage(@RequestParam(value = "page", required = false) Integer page,
                                    Model model,
                                    @Valid @ModelAttribute("postFilter") PostFilterDto filterDto, HttpSession session) {
        PostFilter postFilter = new PostFilter(
                filterDto.getTitle(),
                filterDto.getContent(),
                filterDto.getSortBy(),
                filterDto.getSortOrder()
        );
        if (page == null || page == 0) {
            page = 1;
        }
        List<Post> dataList = postService.getPaginatedPosts(page, DEFAULT_PAGE_SIZE, postFilter);

        int totalItems = postService.getAll(postFilter).size();
        int totalPages = (int) Math.ceil((double) totalItems / DEFAULT_PAGE_SIZE);

        model.addAttribute("posts", dataList);
        model.addAttribute("postService", postService);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("postFilter", filterDto);
        model.addAttribute("loggedUser", authenticationHelper.tryPopulateUser(session));
        return "PostsView";
    }

    @GetMapping("/{id}")
    public String getPost(@ModelAttribute User loggedUser, @ModelAttribute SingletonCommentDto singletonCommentDto,
                          @PathVariable int id, Model model, HttpSession session) {

        try {
            Post post = postService.get(id);
            model.addAttribute("comment", singletonCommentDto);
            model.addAttribute("post", post);
            model.addAttribute("postComments", postService.getPostComments(post));
            model.addAttribute("user", authenticationHelper.tryPopulateUser(session));
            model.addAttribute("commentService", commentService);
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
        return "PostsView";
    }

    @GetMapping("/new")
    public String showCreatePostView(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        }
        model.addAttribute("post", new PostDto());
        return "CreatePostView";
    }

    @GetMapping("{id}/like")
    public String likePost(HttpSession session, Model model, @PathVariable int id) {
        try {

            User loggedUser = authenticationHelper.tryGetCurrentUser(session);
            Post postToLike = postService.get(id);
            postService.likePost(loggedUser, postToLike);
            return "redirect:/posts/{id}";
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        }
    }


    @PostMapping("/new")
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
            return "CreatePostView";
        }

        try {
            Post postToCreate = postMapper.fromDto(postDto);
            postService.createPost(postToCreate, user);
            return "redirect:/posts/"+postToCreate.getId();
        } catch (EntityDuplicateException | UserBannedException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 409);
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, Model model, HttpSession session) {

        User user;
        Post post;
        try {
            post = postService.get(id);
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.deletePost(post, user);
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

    @GetMapping("/{id}/update")
    public String updatePost(@PathVariable int id, @Valid UpdatePostDto updatePostDto, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "ErrorView";
        }
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Post postToUpdate = postMapper.fromUpdatePostDto(updatePostDto, id, user);
            postService.updatePost(postToUpdate, user);
            model.addAttribute("postId", id);
            return "redirect:/posts/{id}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        } catch (UserBannedException e) {
            return "ErrorView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


    @PostMapping("/{id}/comment")
    public String addCommentToPost(@ModelAttribute("comment") @Valid CommentDto commentDto,BindingResult bindingResult, @PathVariable int id, Model model, HttpSession session) {
        User user;
        if(bindingResult.hasErrors()){
            return "ErrorView";
        }
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            Post post = postService.get(id);
            Comment comment = commentMapper.fromDto(commentDto);
            commentService.createComment(user, post, comment);
            return "redirect:/posts/{id}";
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

    }
    @GetMapping("{postId}/comment/{id}/update")
    public String updatePostComment(@PathVariable int postId, @ModelAttribute("comment") @Valid CommentDto commentDto, BindingResult bindingResult, @PathVariable int id, HttpSession session, Model model) {
        if(bindingResult.hasErrors()){
            return "ErrorView";
        }
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Comment comment = commentMapper.fromDto(commentDto);
            comment.setId(id);
            commentService.updateComment(comment, user, id);
            model.addAttribute("postId",postId);
            return "redirect:/posts/{postId}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        } catch (UserBannedException e) {
            return "ErrorView";
        }
    }
    @GetMapping("{postId}/comment/{id}/delete")
    public String deletePostComment(@PathVariable int postId,@PathVariable int id, HttpSession session, Model model) {

        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            commentService.deleteComment(user, id);
            model.addAttribute("postId", postId);
            return "redirect:/posts/{postId}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 404);
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("statusCode", 401);
            return "ErrorView";
        } catch (UserBannedException e){
            return "ErrorView";
        }
    }
}
