package com.sportdataauth.repository;

import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.value.Email;

public interface UserRepository {
    User findByEmail(Email email);
    User findById(UUID id);
    void save(User user);
}

