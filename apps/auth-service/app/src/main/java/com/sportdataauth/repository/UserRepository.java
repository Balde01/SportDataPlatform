package com.sportdataauth.repository;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.value.Email;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void insert(User user);

    void update(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(Email email);

    boolean existsById(UUID id);

    boolean existsByEmail(Email email);
}
