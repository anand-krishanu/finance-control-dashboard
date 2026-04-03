package com.financecontrol.service;

import com.financecontrol.model.*;
import com.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Handles user stuff and makes sure we do not save raw passwords.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Makes a new user account and scrambles their password first.
     *
     * @param user the new kid trying to sign up
     * @return the newly saved user with a hidden password
     * @throws IllegalArgumentException if someone already took that name
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Sends out literally every user we have on file.
     *
     * @return a massive list of everyone
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gives a user a quick promotion or demotes them.
     *
     * @param id the ID of the user we are messing with
     * @param role the new job title for them
     * @return the newly updated user
     * @throws IllegalArgumentException if that user does not even exist
     */
    public User updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Wipes a user completely off the face of the earth.
     *
     * @param id the ID of the unlucky user
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
