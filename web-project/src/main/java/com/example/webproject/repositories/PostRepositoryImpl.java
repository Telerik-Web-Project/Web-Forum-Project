package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.*;
import com.example.webproject.repositories.contracts.PostRepository;
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
public class PostRepositoryImpl implements PostRepository {

    private static final String MASTER_USER_ID = "1";
    private static final int POST_PER_PAGE = 5;
    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getAll(PostFilter filter) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            filter.getTitle().ifPresent(value -> {
                filters.add("title like :title");
                params.put("title", String.format("%%%s%%", value));
            });

            filter.getContent().ifPresent(value -> {
                filters.add("content like :content");
                params.put("content", String.format("%%%s%%", value));
            });

            StringBuilder queryString = new StringBuilder("from Post");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filter));
            Query<Post> query = session.createQuery(queryString.toString(), Post.class);
            query.setProperties(params);
            return query.list();
        }
    }

    //TODO check if constant  MASTER_USER_ID works properly !!!
    public List<Post> getPostsAsAnonymousUser() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post where postCreator.id!=" + MASTER_USER_ID + " order by id desc limit 10", Post.class);
            return query.list();
        }
    }

    @Override
    public List<Post> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post", Post.class);
            return query.list();
        }
    }

    @Override
    public int getLikesCount(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createQuery(
                    "select count(l) from Post p join p.likes l where p.id = :postId", Integer.class);
            return query.list().size();
        }
    }

    @Override
    public List<Comment> getPostComments(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> result = session.createQuery("from Comment where post.id=:id", Comment.class);
            result.setParameter("id", post.getId());
            return result.list();
        }
    }

    @Override
    public Post get(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public Post createPost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public Post updatePost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public Post deletePost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            deletePostComments(post.getId());
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
            return post;
        }
    }

    private void deletePostComments(int postId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String queryString = "DELETE FROM comments WHERE post_id = :postId";
            Query<?> query = session.createNativeQuery(queryString, Comment.class);
            query.setParameter("postId", postId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(PostFilter filter) {
        if (filter.getSortBy().isEmpty()) {
            return "";
        }
        String orderBy;
        switch (filter.getSortBy().get()) {
            case "title":
                orderBy = "title";
                break;
            case "content":
                orderBy = "content";
                break;
            default:
                return "";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (filter.getSortOrder().isPresent() && filter.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }

    @Override
    public List<Post> getTenMostCommentedPosts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("SELECT c.post " +
                    "FROM Comment c " +
                    "GROUP BY c.post " +
                    "ORDER BY COUNT(c.id) DESC", Post.class);
            query.setMaxResults(10);
            return query.list();

        }
    }

    @Override
    public List<Post> getPostsWithTags(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String queryString = "select p.* from posts p " +
                    "join forum.posts_tags p2 on p.post_id = p2.post_id " +
                    "where p2.tag_id = :id";
            Query<Post> query = session.createNativeQuery(queryString,Post.class);
            query.setParameter("id", tag.getId());
            session.getTransaction().commit();
            return query.list();
        }
    }
    public List<Post> getPaginatedPosts(int page){
        try (Session session = sessionFactory.openSession()) {
            int offset = (page - 1) * POST_PER_PAGE;
            Query<Post> query = session.createQuery("FROM Post", Post.class);
            query.setFirstResult(offset);
            query.setMaxResults(POST_PER_PAGE);
            return query.list();
        }
    }
}

