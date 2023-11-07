package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.UserFilter;
import com.example.webproject.models.Post;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.UserRepository;
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
    public List<User> getAll(UserFilter userFilter) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            userFilter.getUsername().ifPresent(value -> {
                filters.add("username like :username");
                params.put("username", String.format("%%%s%%", value));
            });

            userFilter.getEmail().ifPresent(value -> {
                filters.add("email like :email");
                params.put("email", String.format("%%%s%%", value));
            });
            userFilter.getFirstName().ifPresent(value -> {
                filters.add("first_name like :first_name");
                params.put("first_name", String.format("%%%s%%", value));
            });
            StringBuilder queryString = new StringBuilder("from User");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(userFilter));
            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where " +
                    "username =:username", User.class);
            query.setParameter("username", username);
            List<User> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return users.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where " +
                    "email =:email", User.class);
            query.setParameter("email", email);
            List<User> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return users.get(0);
        }
    }

    @Override
    public List<Post> getUserPosts(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> result = session.createQuery("from Post " +
                    "where postCreator.id=:id", Post.class);
            result.setParameter("id", user.getId());
            List<Post> userPosts = result.list();
            return new ArrayList<>(userPosts);
        }
    }

    @Override
    public User createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User deleteUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            updateUserComments(user.getId());
            updateUserPosts(user.getId());
            deleteUserLikedPostsByUserId(user.getId());
            deleteUserPhones(user.getId());
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public int getUsersCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "select count(*) from User ", Long.class);
            return query.uniqueResult().intValue();
        }
    }


    private void deleteUserLikedPostsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String queryString = "DELETE FROM liked_posts WHERE user_id = :userId";
            Query<?> query = session.createNativeQuery(queryString, User.class);
            query.setParameter("userId", userId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void deleteUserPhones(int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String queryString = "DELETE FROM admin_phones WHERE user_id = :userId";
            Query<?> query = session.createNativeQuery(queryString, User.class);
            query.setParameter("userId", userId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }
    private void updateUserComments(int userId) {
        String queryString = "UPDATE COMMENTS SET user_id=1 where user_id=:userId";
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery(queryString, Integer.class)
                    .setParameter("userId",userId).executeUpdate();

            session.getTransaction().commit();
        }
    }
    private void updateUserPosts(int userId) {
        String queryString = "UPDATE POSTS SET user_id=1 where user_id=:userId";
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery(queryString, Integer.class)
                    .setParameter("userId",userId).executeUpdate();
            session.getTransaction().commit();
        }
    }
    private String generateOrderBy(UserFilter userFilter) {
        if (userFilter.getSortBy().isEmpty()) {
            return "";
        }
        String orderBy;
        switch (userFilter.getSortBy().get()) {
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

        if (userFilter.getSortOrder().isPresent() && userFilter.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }

    public List<User> getPaginatedUsers(int page, int postPerPage){
        try (Session session = sessionFactory.openSession()) {
            int offset = (page - 1) * postPerPage;
            Query<User> query = session.createQuery("FROM User where id!=1", User.class);
            query.setFirstResult(offset);
            query.setMaxResults(postPerPage);
            return query.list();
        }
    }

}
