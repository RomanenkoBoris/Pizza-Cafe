package com.pizza.pizzaproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Entity
@Table(name = "pizzas")
@NoArgsConstructor
@Getter
@Setter
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    @Length(min = 3, max = 50, message = "Name of the pizza must be from 3 to 50 symbols")
    @NotNull(message = "Enter pizza name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    @NotNull(message = "Enter pizza size")
    private PizzaSize size;

    @Column(length = 100, nullable = false)
    @Length(min = 10, max = 100, message = "Key ingredients of the pizza must be from 10 to 100 symbols")
    @NotNull(message = "Enter pizza key ingredients")
    private String keyIngredients;

    @Column(nullable = false)
    @Positive(message = "Price should be positive")
    @DecimalMin(value = "3", message = "Price cannot be cheaper then 3")
    @NotNull(message = "Enter price of the pizza")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

}
