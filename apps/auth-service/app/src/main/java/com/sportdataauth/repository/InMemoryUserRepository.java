package com.sportdataauth.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sportdataauth.domain.entity.User;
import com.sportdataauth.domain.exception.EmailAlreadyExistsException;
import com.sportdataauth.domain.exception.InvalidRequestException;
import com.sportdataauth.domain.exception.UserAlreadyExistsException;
import com.sportdataauth.domain.exception.UserNotFoundException;
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
        if (id == null) return Optional.empty();
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
    public void insert(User user) {
        if (user == null) {
            throw InvalidRequestException.nullValue("user");
        }
        if (existsById(user.getId())) {
            throw new UserAlreadyExistsException();
        }
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        byId.put(user.getId(), user);
        idByEmail.put(user.getEmail(), user.getId());

    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw InvalidRequestException.nullValue("user");
        }
        if (!existsById(user.getId())) {
            throw new UserNotFoundException();
        }
        byId.put(user.getId(), user);
        idByEmail.put(user.getEmail(), user.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            throw InvalidRequestException.nullValue("id");
        }
        return byId.containsKey(id);
    }

    @Override
    public boolean existsByEmail(Email email) {
        if (email == null) {
            throw InvalidRequestException.nullValue("email");
        }
        return idByEmail.containsKey(email);
    }

}
