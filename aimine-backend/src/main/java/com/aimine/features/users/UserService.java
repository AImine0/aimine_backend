package com.aimine.features.users;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository r) {
        this.repo = r;
    }

    public Optional<User> findByEmail(String e) {
        return repo.findByEmail(e);
    }
}