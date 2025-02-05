package com.example.Twizzy.Services;

import com.example.Twizzy.Entities.Role;
import com.example.Twizzy.Entities.User;
import com.example.Twizzy.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Manually created constructor to initialize userRepository and passwordEncoder
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user, boolean isAdmin) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles based on isAdmin flag
        user.setRoles(isAdmin ? Set.of(Role.USER, Role.ADMIN) : Set.of(Role.USER));

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
