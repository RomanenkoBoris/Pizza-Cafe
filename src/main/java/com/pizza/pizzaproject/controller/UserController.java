package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal Principal principal){
        log.info("Admin {} requested all users at {}", principal.getName(), LocalDateTime.now());
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
