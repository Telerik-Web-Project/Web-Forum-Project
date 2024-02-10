package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.Role;

import com.example.webproject.repositories.contracts.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory;
    @Autowired
    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Role role = session.get(Role.class, id);
            if (role == null) {
                throw new EntityNotFoundException("Role", id);
            }
            return role;
        }
    }

    @Override
    public Role findByAuthority(String authority) {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("from Role where " +
                    "authority =:authority", Role.class);
            query.setParameter("authority", authority);
            List<Role> roles = query.list();
            if (roles.isEmpty()) {
                throw new EntityNotFoundException("Role", "authority", authority);
            }
            return roles.get(0);
        }
    }
}
