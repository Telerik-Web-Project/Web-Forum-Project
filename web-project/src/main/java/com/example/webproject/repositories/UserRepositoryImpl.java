package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public List<User> getAll(String username, String firstname, String email) {
        try(Session session = sessionFactory.openSession()){
            Query<User> query = session.createQuery("from User",User.class);
            return query.list();
        }
    }
    @Override
    public List<Post> getUserPosts(int userId) {
        /*User user = get(userId);
        List <Post> userPosts = user.getUserPosts().stream().toList();
        if(userPosts.isEmpty()){
            throw new EntityNotFoundException(user.getId());
        }
        return userPosts;*/
        try(Session session = sessionFactory.openSession()){
            Query<Post> result = session.createQuery("from Post " +
                    "where user.id=:id",Post.class);
            List <Post> userPosts= result.list();
            if(userPosts.isEmpty()){
                throw new EntityNotFoundException(userId);
            }
            return userPosts;
        }
    }

    @Override
    public User get(int id) {
        try(Session session = sessionFactory.openSession()){
            User user = session.get(User.class,id);
            if(user == null){
                throw new EntityNotFoundException("User",id);
            }
            return user;
        }
    }

    @Override
    public User getByUsername(String username) {
        try(Session session = sessionFactory.openSession()){
            Query<User> query = session.createQuery("from User where " +
                    "username =:username",User.class);
            query.setParameter("username",username);
            List<User> users = query.list();
            if(users.isEmpty()){
                throw new EntityNotFoundException("User","username",username);
            }
            return users.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try(Session session = sessionFactory.openSession()){
            Query<User> query = session.createQuery("from User where " +
                    "email =:email",User.class);
            query.setParameter("email",email);
            List<User> users = query.list();
            if(users.isEmpty()){
                throw new EntityNotFoundException("User","email",email);
            }
            return users.get(0);
        }
    }
    @Override
    public User createUser(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User updateUser(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User deleteUser(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
            return user;
        }
    }
}
