package com.pizza.pizzaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cafes")
@NoArgsConstructor
@Getter
@Setter
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    @Length(min = 3, max = 50, message = "Name of the cafe must be from 3 to 50 symbols")
    @NotNull(message = "Enter cafe name")
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    @Length(min = 10, max = 100, message = "Location of the cafe must be from 10 to 100 symbols")
    @NotNull(message = "Enter cafe location")
    private String location;

    @Column(length = 30, nullable = false, unique = true)
    @Length(min = 10, max = 30, message = "Phone number of the cafe must be from 10 to 30 symbols")
    @NotNull(message = "Enter cafe phone number")
    private String phone;

    @OneToMany(mappedBy = "cafe", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Pizza> pizzas = new HashSet<>();

    public void addPizza(Pizza pizza){
        pizzas.add(pizza);
        pizza.setCafe(this);
    }

    public void removePizza(Long pizzaId){
        Pizza existingPizza = pizzas.stream().filter(pizza -> pizza.getId().equals(pizzaId)).findFirst().orElse(null);
        if (existingPizza!=null) {
            pizzas.remove(existingPizza);
            existingPizza.setCafe(null);
        }
    }

}
