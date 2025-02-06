package com.example.Twizzy.Services;

import com.example.Twizzy.Entities.Role;
import com.example.Twizzy.Entities.User;
import com.example.Twizzy.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a User
    public User registerUser(User user, boolean isAdmin) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles based on isAdmin flag
        Set<Role> roles = isAdmin ? Set.of(Role.USER, Role.ADMIN) : Set.of(Role.USER);
        user.setRoles(roles);

        // Save the user
        User savedUser = userRepository.save(user);

        // Log or return the user to verify roles
        System.out.println("User roles: " + savedUser.getRoles());

        return savedUser;
    }


    // Get a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get a user by ID
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update user details
    public User updateUser(String id, User updatedUser) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            updatedUser.setId(id); // Ensure the ID is preserved
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Re-encode password
            return userRepository.save(updatedUser);
        }
        return null; // User not found
    }

    // Delete a user
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // For Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
