package com.pizza.pizzaproject.dto;

import com.pizza.pizzaproject.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record UserCreateOrUpdateDTO (
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 50, message = "First name must be less than 50 characters")
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
        String first_name,
        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 50, message = "First name must be less than 50 characters")
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
        String second_name,
        @NotBlank(message = "Email cannot be blank")
        @Size(max = 30, message = "Email must be less than 50 characters")
        @Email
        String email,
        @NotBlank(message = "Username cannot be blank")
        @Size(max = 30, message = "Username must be less than 30 characters")
        @Pattern(regexp = "^[A-Za-z0-9_.]+$", message = "Username can contain only letters, numbers, underscores, and dots")
        String username,
        @NotBlank(message = "Password cannot be blank")
        @Size(max = 20, message = "Password must be less than 20 characters")
        String password,

        @NotNull(message = "UserRole cannot be null")
        @Enumerated(EnumType.STRING)
        @Length(min = 9, max = 10, message = "User role must be between 9 and 10 symbols")
        String role
) {}
