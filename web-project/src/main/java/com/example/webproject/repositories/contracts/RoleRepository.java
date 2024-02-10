package com.example.webproject.repositories.contracts;

import com.example.webproject.models.Role;

public interface RoleRepository {

    Role findById(int id);
    Role findByAuthority(String authority);
}
