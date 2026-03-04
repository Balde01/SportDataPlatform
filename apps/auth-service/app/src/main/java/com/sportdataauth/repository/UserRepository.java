package com.sportdataauth.repository;

import java.util.Optional;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.value.Email;

public interface UserRepository {
    Optional<User> findByEmail(Email email);
    Optional<User> findById(UUID id);
    void save(User user);
}

