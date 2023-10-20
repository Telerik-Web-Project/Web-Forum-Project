package com.example.webproject.services;

import com.example.webproject.helpers.ValidationHelper;
import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    public static final String AUTHENTICATION_ERROR = "Only admins or the creator of the post can modify it.";

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAll(UserFilter userFilter) {
        return postRepository.getAll(userFilter);
    }

    @Override
    public Post get(int id) {
        return postRepository.get(id);
    }

    @Override
    public void createPost(Post post, User user) {
        ValidationHelper.checkIfBanned(user);
        post.setPostCreator(user);
        postRepository.createPost(post);
    }

    @Override
    public void updatePost(Post post, User user) {
        ValidationHelper.validateModifyPermissions(postRepository,post, user);
        ValidationHelper.checkIfBanned(user);
        ValidationHelper.validatePostExists(postRepository,post);

        postRepository.updatePost(post);
    }

    @Override
    public void deletePost(Post post, User user) {
        ValidationHelper.validateModifyPermissions(postRepository,post, user);
        ValidationHelper.checkIfBanned(user);
        postRepository.deletePost(post);
    }

    @Override
    public void likePost(User user, Post post) {
        ValidationHelper.checkIfBanned(user);
        if(!post.getLikes().contains(user)){
            post.likePost(user);
        }else {
            post.dislikePost(user);
        }
        postRepository.updatePost(post);
    }

    public int getLikesCount(Post post) {
        ValidationHelper.validatePostExists(postRepository,post);
        return postRepository.getLikesCount(post);
    }

    /*private void verifyPostExists(Post post) {
        Post existingPost = postRepository.get(post.getId());
        if(existingPost.getId() != post.getId()) {
            throw new EntityNotFoundException("Post", "id", String.valueOf(post.getId()));
        }
    }*/

  /*  private void checkModifyPermissions(Post post, User user) {
        Post postToUpdate = postRepository.get(post.getId());
        if (!(user.isAdmin() || postToUpdate.getPostCreator().equals(user))) {
            throw new AuthorizationException(PostServiceImpl.AUTHENTICATION_ERROR);
        }
    }*/

   /* private void checkIfBanned(User user) {
        if (user.isBlocked()) {
            throw new UserBannedException();
        }
    }*/
}
