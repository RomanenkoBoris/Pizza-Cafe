package com.pizza.pizzaproject.entity;

import com.pizza.pizzaproject.enums.PizzaSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private PizzaSize size;

    @Column(length = 100, nullable = false)
    private String keyIngredients;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    public Pizza(String name, PizzaSize size, String keyIngredients, BigDecimal price, Cafe cafe) {
        this.name = name;
        this.size = size;
        this.keyIngredients = keyIngredients;
        this.price = price;
        this.cafe = cafe;
    }

}
