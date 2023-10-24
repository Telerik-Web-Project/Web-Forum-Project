package com.example.webproject.repositories;

import com.example.webproject.exceptions.EntityNotFoundException;
import com.example.webproject.models.Phone;
import com.example.webproject.models.User;
import com.example.webproject.repositories.contracts.PhoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhoneRepositoryImpl implements PhoneRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PhoneRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Phone findPhone(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Phone> query = session.createQuery("from Phone where " +
                    "phoneNumber =:phoneNumber", Phone.class);
            query.setParameter("phoneNumber", phoneNumber);
            List<Phone> phones = query.list();
            if (phones.isEmpty()) {
                throw new EntityNotFoundException("User", "phone number", phoneNumber);
            }
            return phones.get(0);
        }
    }


    @Override
    public void updatePhone(Phone oldPhone) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(oldPhone);
            session.getTransaction().commit();
        }
    }


    @Override
    public Phone findPhone(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Phone> query = session.createQuery("from Phone where " +
                    "adminUser =:user", Phone.class);
            query.setParameter("user", user);
            List<Phone> phones = query.list();
            if (phones.isEmpty()) {
                throw new EntityNotFoundException("User", "phone number", "");
            }
            return phones.get(0);
        }
    }


    @Override
    public void deletePhone(Phone phone) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(phone);
            session.getTransaction().commit();
        }
    }


    @Override
    public Phone createPhone(Phone phone) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(phone);
            session.getTransaction().commit();
            return phone;
        }
    }
}
