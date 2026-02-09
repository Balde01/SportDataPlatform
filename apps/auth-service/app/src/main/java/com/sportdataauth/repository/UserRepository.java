package com.sportdataauth.repository;

import java.util.UUID;

import com.sportdataauth.model.User;

public interface UserRepository {
    User findByEmail(String email);
    User findById(UUID id);
    void save(User user);
}

