package com.example.quizbowl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private static final String ADMIN_USER = "administrator";
    private static final String ADMIN_PASS = "password";
    private final Map<String, UserRecord> userStore = new ConcurrentHashMap<>();
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public AuthService() {
        userStore.put(ADMIN_USER, new UserRecord(ADMIN_USER, ADMIN_PASS, "ADMIN"));
    }

    public synchronized LoginResponse register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password required");
        }
        if (ADMIN_USER.equals(username) || userStore.containsKey(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        UserRecord record = new UserRecord(username, password, "USER");
        userStore.put(username, record);
        return createSession(username, record.role());
    }

    public synchronized LoginResponse login(String username, String password) {
        UserRecord record = userStore.get(username);
        if (record != null && record.password().equals(password)) {
            return createSession(record.username(), record.role());
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
        UserRecord record = userStore.get(info.username());
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session");
        }
        String targetUsername = info.username();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(info.username())) {
            if (userStore.containsKey(newUsername) || ADMIN_USER.equals(newUsername)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }
            userStore.remove(info.username());
            userStore.put(newUsername, new UserRecord(newUsername, record.password(), record.role()));
            targetUsername = newUsername;
            sessions.put(token, new SessionInfo(newUsername, record.role()));
        }
        if (newPassword != null && !newPassword.isBlank()) {
            UserRecord updated = new UserRecord(targetUsername, newPassword, record.role());
            userStore.put(targetUsername, updated);
        }
        UserRecord updatedFinal = userStore.get(targetUsername);
        return new LoginResponse(token, updatedFinal.username(), updatedFinal.role());
    }

    private LoginResponse createSession(String username, String role) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new SessionInfo(username, role));
        return new LoginResponse(token, username, role);
    }

    record SessionInfo(String username, String role) {}

    record UserRecord(String username, String password, String role) {}
}
