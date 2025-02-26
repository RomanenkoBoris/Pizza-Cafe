package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "User Management", description = "Endpoints for managing users")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Admin {} requested all users at {}", getAuth().getName(), LocalDateTime.now());
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get user by ID", description = "Retrieves details of a user by their ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (isOwnerOrAdmin(user.getUsername()))
            return ResponseEntity.ok(user);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Create a new user", description = "Adds a new user to the system")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateOrUpdateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @Operation(summary = "Update a user", description = "Updates an existing user by username")
    @PutMapping("/{userName}")
    public ResponseEntity<User> updateUser(@PathVariable String userName, @RequestBody UserCreateOrUpdateDTO dto){
        if (isOwnerOrAdmin(userName))
            return ResponseEntity.ok(userService.updateUser(userName, dto));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id){
        User user = userService.getUserById(id);
        if (isOwnerOrAdmin(user.getUsername())){
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private boolean isOwnerOrAdmin(String username){
        Authentication authentication = getAuth();
        boolean isOwner = username.equals(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return isOwner || isAdmin;
    }

    private Authentication getAuth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }


}
