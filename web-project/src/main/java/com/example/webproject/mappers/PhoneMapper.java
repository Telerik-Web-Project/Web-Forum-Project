package com.example.webproject.mappers;

import com.example.webproject.dtos.PhoneDto;
import com.example.webproject.models.Phone;
import com.example.webproject.models.User;
import org.springframework.stereotype.Component;

@Component
public class PhoneMapper {

    public PhoneMapper() {
    }
    public Phone fromDto(PhoneDto phoneDto, User loggedUser){
        Phone phone = new Phone();
        phone.setPhoneNumber(phoneDto.getPhoneNumber());
        phone.setAdminUser(loggedUser);
        return phone;
    }

}
