package com.example.pak.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.pak.model.User;

@Service
public class UserService {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>(); 
    public UserService() {
        // default admin credentials (username: admin, password: 12345)
        users.put("admin", new User("admin","12345","Pentadbir"));
        users.put("user", new User("user","user123","Ahli"));
    }

    public Optional<User> authenticate(String username, String password) {
        User u = users.get(username);
        if (u != null && u.getPassword().equals(password)) return Optional.of(u);
        return Optional.empty();
    }

    public User createUser(String username, String password, String role) {
        User u = new User(username, password, role);
        users.put(username, u);
        return u;
    }

    public String createTokenFor(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public Optional<User> findByToken(String token) {
        String username = tokens.get(token);
        if (username == null) return Optional.empty();
        return Optional.ofNullable(users.get(username));
    }

    public void revokeToken(String token){
        tokens.remove(token);
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public java.util.Collection<User> getAllUsers() {
        return users.values();
    }

    public void updateUser(String oldUsername, String newUsername, String password, String role) {
        users.remove(oldUsername);
        User u = new User(newUsername, password, role);
        users.put(newUsername, u);
    }

    public void deleteUser(String username) {
        users.remove(username);
        tokens.entrySet().removeIf(e -> e.getValue().equals(username));
    }
}
