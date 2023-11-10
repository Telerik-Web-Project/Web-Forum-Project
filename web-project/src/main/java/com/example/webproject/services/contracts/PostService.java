package com.example.webproject.services.contracts;

import com.example.webproject.models.*;

import java.util.List;

public interface PostService {
    List<Post> getAll(PostFilter filter);
    Post get(int id);
    List<Post> getPostsWithTag(String tag);
    void addTagToPost(Post post, Tag tag, User loggedUser);
    void deleteTagFromPost(Post post, User loggedUser, Tag tag);
    void createPost(Post post, User user);
    void updatePost(Post post, User user);
    void deletePost(Post post, User user);
    void likePost(User user, Post post);
    List<Comment> getPostComments(Post post);
    List<Post> getTenMostCommentedPosts();
    List<Post> getMostRecentPosts();
    List<Post> getPaginatedPosts(int page, int postsPerPage, PostFilter postFilter);

}
