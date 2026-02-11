package com.sportdataauth.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sportdataauth.model.User;

public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> byId = new HashMap<>();
    private final Map<String, UUID> idByEmail = new HashMap<>();

    @Override
    public User findByEmail(String email) {
        UUID id = idByEmail.get(email);
        if (id == null) {
            return null;
        }
        return byId.get(id);
    }

    @Override
    public User findById(UUID id) {
        byId.get(id);
        return byId.get(id);
    }

    @Override
    public void save(User user) {
        byId.put(user.getId(), user);
        idByEmail.put(user.getEmail(), user.getId());
        System.out.println("Saved user: " + user.getEmail());
    }

}
