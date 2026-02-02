package com.sportdataauth.repository;

import com.sportdataauth.model.User;

public interface UserRepository {
    User findByEmail(String email);
    User findById(String id);
    void save(User user);
}

