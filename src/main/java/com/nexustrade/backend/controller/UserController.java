package com.nexustrade.backend.controller;

import com.nexustrade.backend.dto.LoginRequest;
import com.nexustrade.backend.model.User;
import com.nexustrade.backend.security.JwtUtil;
import com.nexustrade.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String sayHello() {
        return "NexusTrade Backend is running successfully!";
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticateUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (isAuthenticated) {
            // Generate and return the secure JWT token
            String token = jwtUtil.generateToken(loginRequest.getEmail());
            return ResponseEntity.ok().body("Login successful! Your JWT Token: " + token);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
    }
}