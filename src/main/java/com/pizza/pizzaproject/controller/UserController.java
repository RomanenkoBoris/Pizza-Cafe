package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.service.UserService;
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

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal Principal principal) {
        log.info("Admin {} requested all users at {}", principal.getName(), LocalDateTime.now());
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @AuthenticationPrincipal Principal principal) {
        User user = userService.getUserById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = user.getUsername().equals(principal.getName());
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (isOwner || isAdmin)
            return ResponseEntity.ok(user);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateOrUpdateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PutMapping("/{userName}")
    @PreAuthorize("#userName == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable String userName, @RequestBody UserCreateOrUpdateDTO dto, @AuthenticationPrincipal Principal principal){
        return ResponseEntity.ok(userService.updateUser(userName, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
