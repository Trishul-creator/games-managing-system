package com.example.quizbowl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private static final String ADMIN_USER = "administrator";
    private static final String ADMIN_PASS = "password";
    private final Map<String, SessionInfo> sessions = new java.util.concurrent.ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        seedAdmin();
    }

    private void seedAdmin() {
        userRepository.findById(ADMIN_USER).orElseGet(() -> {
            UserEntity u = new UserEntity();
            u.setUsername(ADMIN_USER);
            u.setPassword(ADMIN_PASS);
            u.setRole("ADMIN");
            return userRepository.save(u);
        });
    }

    public synchronized LoginResponse register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password required");
        }
        if (ADMIN_USER.equals(username) || userRepository.existsById(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        UserEntity record = new UserEntity();
        record.setUsername(username);
        record.setPassword(password);
        record.setRole("USER");
        userRepository.save(record);
        return createSession(username, record.getRole());
    }

    public synchronized LoginResponse login(String username, String password) {
        UserEntity record = userRepository.findById(username).orElse(null);
        if (record != null && record.getPassword().equals(password)) {
            return createSession(record.getUsername(), record.getRole());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    public synchronized void requireAdmin(String token) {
        SessionInfo info = sessions.get(token);
        if (info == null || !"ADMIN".equals(info.role())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin token required");
        }
    }

    public synchronized String roleFor(String token) {
        SessionInfo info = sessions.get(token);
        return info != null ? info.role() : null;
    }

    public synchronized LoginResponse updateProfile(String token, String newUsername, String newPassword) {
        SessionInfo info = sessions.get(token);
        if (info == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session");
        }
        UserEntity record = userRepository.findById(info.username()).orElse(null);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session");
        }
        String targetUsername = info.username();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(info.username())) {
            if (userRepository.existsById(newUsername) || ADMIN_USER.equals(newUsername)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }
            userRepository.deleteById(info.username());
            UserEntity updated = new UserEntity();
            updated.setUsername(newUsername);
            updated.setPassword(record.getPassword());
            updated.setRole(record.getRole());
            userRepository.save(updated);
            targetUsername = newUsername;
            sessions.put(token, new SessionInfo(newUsername, record.getRole()));
        }
        if (newPassword != null && !newPassword.isBlank()) {
            UserEntity updated = userRepository.findById(targetUsername).orElse(new UserEntity());
            updated.setUsername(targetUsername);
            updated.setPassword(newPassword);
            updated.setRole(record.getRole());
            userRepository.save(updated);
        }
        UserEntity updatedFinal = userRepository.findById(targetUsername).orElse(record);
        return new LoginResponse(token, updatedFinal.getUsername(), updatedFinal.getRole());
    }

    private LoginResponse createSession(String username, String role) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new SessionInfo(username, role));
        return new LoginResponse(token, username, role);
    }

    record SessionInfo(String username, String role) {}
}
