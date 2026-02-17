package com.sportdataauth.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.value.Email;

public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> byId = new HashMap<>();
    private final Map<Email, UUID> idByEmail = new HashMap<>();

    @Override
    public User findByEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }
        UUID id = idByEmail.get(email);
        if (id == null) {
            return null;
        }
        return byId.get(id);
    }

    @Override
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }
        return byId.get(id);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("INVALID_REQUEST");
        }
        byId.put(user.getId(), user);
        idByEmail.put(user.getEmail(), user.getId());
    }

}
