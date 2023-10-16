package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.Comment;
import com.example.webproject.models.Post;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class PostRepositoryImpl implements PostRepository{

    private final SessionFactory sessionFactory;

    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post", Post.class);
            return query.list();
        }
    }

    @Override
    public List<Comment> getAllComments() {
        try(Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment", Comment.class);
            return query.list();
        }
    }

    @Override
    public Post get(int id) {
        try(Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public Post createPost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public Post updatePost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public Post deletePost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
            return post;
        }
    }
}
