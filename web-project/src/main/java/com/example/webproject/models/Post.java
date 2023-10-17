package com.example.webproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User postCreator;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "likedPosts",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> likes;

    public Post() {
    }

    public int getLikesCount() {
        return likes.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    public User getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(User user) {
        this.postCreator = user;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> usersWhoLiked) {
        this.likes = usersWhoLiked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
