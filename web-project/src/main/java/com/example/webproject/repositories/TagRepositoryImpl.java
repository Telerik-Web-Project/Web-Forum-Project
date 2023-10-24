package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.Tag;
import com.example.webproject.repositories.contracts.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private final SessionFactory sessionFactory;
    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag get(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("from Tag where name = :name", Tag.class);
            query.setParameter("name", name);

            List<Tag> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Tag", "name", name);
            }
            return result.get(0);
        }
    }

    @Override
    public Tag createTag(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
            return tag;
        }
    }
}
