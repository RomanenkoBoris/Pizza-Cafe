package com.pizza.pizzaproject.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record PizzaCreateOrUpdateDto(
        @Length(min = 3, max = 50, message = "Name of the pizza must be from 3 to 50 symbols")
        @NotNull(message = "Enter pizza name")
        String name,

        @NotNull(message = "Enter pizza size")
        @Length(min = 5, max = 6, message = "Size of the pizza must be from 5 to 6 symbols. There are 3 available sizes: small, medium and large")
        String size,

        @Length(min = 10, max = 100, message = "Key ingredients of the pizza must be from 10 to 100 symbols")
        @NotNull(message = "Enter pizza key ingredients")
        String keyIngredients,

        @Positive(message = "Price should be positive")
        @DecimalMin(value = "3", message = "Price cannot be cheaper then 3")
        @NotNull(message = "Enter price of the pizza")
        BigDecimal price,

        @Positive(message = "Cafe ID should be positive")
        @NotNull(message = "Enter ID of the cafe")
        Long cafeId
){}
