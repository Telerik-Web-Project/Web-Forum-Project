package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.FilterOptions;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public List<User> getAll(FilterOptions filterOptions) {
        try(Session session = sessionFactory.openSession()){
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                filters.add("username like :username");
                params.put("username", String.format("%%%s%%", value));
            });

            filterOptions.getEmail().ifPresent(value -> {
                filters.add("email like :email");
                params.put("email", String.format("%%%s%%", value));
            });
            filterOptions.getFirstName().ifPresent(value -> {
                filters.add("first_name like :first_name");
                params.put("first_name", String.format("%%%s%%", value));
            });
            StringBuilder queryString = new StringBuilder("from User");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));
            Query<User> query = session.createQuery(queryString.toString(),User.class);
            query.setProperties(params);
            return query.list();
        }
    }
    @Override
    public List<Post> getUserPosts(User user) {
        try(Session session = sessionFactory.openSession()){
            Query<Post> result = session.createQuery("from Post " +
                    "where postCreator.id=:id",Post.class);
            result.setParameter("id",user.getId());
            List <Post> userPosts= result.list();
            if(userPosts.isEmpty()){
                throw new EntityNotFoundException(user.getId());
            }
            return userPosts;
        }
    }
    @Override
    public User getById(int id) {
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
    private String generateOrderBy(FilterOptions filterOptions) {
        if (!filterOptions.getSortBy().isPresent()) {
            return "";
        }

        String orderBy;
        switch (filterOptions.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "email";
                break;
            case "firstName":
                orderBy = "firstName";
                break;
            default:
                return "";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getSortOrder().isPresent() && filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }

}
