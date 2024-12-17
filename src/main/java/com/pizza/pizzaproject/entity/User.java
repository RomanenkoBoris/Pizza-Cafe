package com.pizza.pizzaproject.entity;

import com.pizza.pizzaproject.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private String first_name;
    @Column(length = 50, nullable = false)
    private String second_name;
    @Column(length = 30, unique = true, nullable = false)
    private String email;

    @Column(length = 30, unique = true, nullable = false)
    private String username;

    @Column(length = 20, nullable = false)
    private String password;
    @Column(length = 10, nullable = false)
    private UserRole role;

}
