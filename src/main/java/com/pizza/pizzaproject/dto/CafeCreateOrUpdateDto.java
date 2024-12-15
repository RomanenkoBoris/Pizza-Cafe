package com.pizza.pizzaproject.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CafeCreateOrUpdateDto(
        @Length(min = 3, max = 50, message = "Name of the cafe must be from 3 to 50 symbols")
        @NotNull(message = "Enter cafe name")
        String name,

        @Length(min = 10, max = 100, message = "Location of the cafe must be from 10 to 100 symbols")
        @NotNull(message = "Enter cafe location")
        String location,
        @Length(min = 10, max = 30, message = "Phone number of the cafe must be from 10 to 30 symbols")
        @NotNull(message = "Enter cafe phone number")
        String phone
) {}
