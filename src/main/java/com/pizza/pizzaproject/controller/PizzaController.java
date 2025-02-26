package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.service.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pizza Management", description = "Endpoints for managing pizzas")
@RestController
@RequestMapping("/api/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @Operation(summary = "Get all pizzas", description = "Retrieves a list of all available pizzas")
    @GetMapping
    public ResponseEntity<List<Pizza>> getAllPizzas() {

        return ResponseEntity.ok(pizzaService.getAllPizzas());
    }

    @Operation(summary = "Get pizza by ID", description = "Retrieves details of a pizza by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Pizza> getPizzaById(@PathVariable Long id) {
        return ResponseEntity.ok(pizzaService.getPizzaById(id));
    }

    @Operation(summary = "Create a new pizza", description = "Adds a new pizza to the system")
    @PostMapping
    public ResponseEntity<Pizza> createPizza(@RequestBody @Valid PizzaCreateOrUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pizzaService.createPizza(dto));
    }

    @Operation(summary = "Update a pizza", description = "Updates an existing pizza by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<Pizza> updatePizza(@PathVariable Long id, @RequestBody @Valid PizzaCreateOrUpdateDto dto) {
        return ResponseEntity.ok(pizzaService.updatePizza(id, dto));
    }

    @Operation(summary = "Delete a pizza", description = "Deletes a pizza by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePizza(@PathVariable Long id) {
        pizzaService.deletePizza(id);
        return ResponseEntity.noContent().build();
    }


}
