package com.hosanna.hotelmanagement.controller;

import com.hosanna.hotelmanagement.model.User;
import com.hosanna.hotelmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hosanna.hotelmanagement.config.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register endpoint
    @PostMapping("/auth/register")
    public String registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return "User registered successfully!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Login endpoint
    @PostMapping("/auth/login")
    public String loginUser(@RequestBody User user) {
        try {
            boolean valid = userService.loginUser(user.getUsername(), user.getPassword());
            if (!valid) return "Invalid credentials!";

            // Generate JWT
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            return token;
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    // Get all users (Admin use-case or testing)
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get a user by ID
    @GetMapping("/users/{id}")
    public Object getUserById(@PathVariable Long id) {
        try {
            return userService.getUserById(id);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Update user by ID
    @PutMapping("/users/{id}")
    public Object updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return userService.updateUser(id, user);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Delete user by ID
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            return userService.deleteUser(id);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
