package com.financecontrol.controller;

import jakarta.validation.Valid;
import com.financecontrol.model.User;
import com.financecontrol.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for dealing with user accounts.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Signs up a brand new user into the database.
     *
     * @param user the incoming user data to save
     * @return the fully saved user object
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**
     * Grabs literally every user we have in the system.
     *
     * @return a massive list of all the users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Removess a user out of the database permanently.
     *
     * @param id the ID of the user getting deleted
     * @return a blank response saying it is gone
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
