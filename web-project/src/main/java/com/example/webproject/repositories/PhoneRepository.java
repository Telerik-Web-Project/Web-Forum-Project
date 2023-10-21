package com.example.webproject.repositories;

import com.example.webproject.models.Phone;
import com.example.webproject.models.User;

public interface PhoneRepository {
    Phone findPhone(String phoneNumber);

    void updatePhone(Phone oldPhone);

    Phone findPhone(User user);

    void deletePhone(Phone phone);

    Phone createPhone(Phone phone);
}
