package com.example.quizbowl;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserCredentialRequest body) {
        return authService.login(body.getUsername(), body.getPassword());
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody UserCredentialRequest body) {
        return authService.register(body.getUsername(), body.getPassword());
    }

    @PostMapping("/update-profile")
    public LoginResponse updateProfile(@RequestBody UpdateProfileRequest body,
                                       @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        return authService.updateProfile(token, body.getNewUsername(), body.getNewPassword());
    }
}
