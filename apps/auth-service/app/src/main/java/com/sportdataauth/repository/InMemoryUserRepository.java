package com.sportdataauth.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.value.Email;

public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> byId = new HashMap<>();
    private final Map<Email, UUID> idByEmail = new HashMap<>();

    @Override
    public Optional<User> findByEmail(Email email) {
        if (email == null) {
            throw InvalidRequestException.nullValue("email");
        }
        UUID id = idByEmail.get(email);
        if (id == null) return null;
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) {
           throw InvalidRequestException.nullValue("id");
        }
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw InvalidRequestException.nullValue("user");
        }
        byId.put(user.getId(), user);
        idByEmail.put(user.getEmail(), user.getId());
    }

}
