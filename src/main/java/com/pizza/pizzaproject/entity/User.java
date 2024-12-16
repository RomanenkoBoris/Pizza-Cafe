package com.pizza.pizzaproject.entity;

import com.pizza.pizzaproject.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must be less than 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    private String first_name;
    @Column(length = 50, nullable = false)
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "First name must be less than 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    private String second_name;

    @Column(length = 30, unique = true, nullable = false)
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 30, message = "Username must be less than 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9_.]+$", message = "Username can contain only letters, numbers, underscores, and dots")
    private String username;

    @Column(length = 20, nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Size(max = 20, message = "Password must be less than 20 characters")
    private String password;
    @Column(length = 10, nullable = false)
    @NotNull(message = "UserRole cannot be null")
    @Enumerated(EnumType.STRING)
    @Length(min = 9, max = 10, message = "User role must be between 9 and 10 symbols. There are 2 available roles: role_user, role_admin")
    private UserRole role;

}
