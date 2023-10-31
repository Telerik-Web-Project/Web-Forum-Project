package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import com.example.webproject.models.PostFilter;
import com.example.webproject.models.Tag;

import java.util.List;

public interface PostRepository {

    List <Post> getAll(PostFilter filter);
    List <Post> getAll();
    Post get(int id);
    Post createPost (Post post);
    Post updatePost (Post post);
    Post deletePost (Post post);
    int getLikesCount(Post post);
    List<Post> getPostsAsAnonymousUser();
    List<Comment> getPostComments(Post post);
    List<Post> getTenMostCommentedPosts();
    List<Post> getPostsWithTags(Tag tag);
}
