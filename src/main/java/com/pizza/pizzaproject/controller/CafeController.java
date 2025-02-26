package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.service.CafeService;
import com.pizza.pizzaproject.entity.Cafe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cafe Management", description = "Endpoints for managing cafes")
@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    @Autowired
    private CafeService cafeService;

    @Operation(summary = "Get all cafes", description = "Retrieves a list of all available cafes")
    @GetMapping
    public ResponseEntity<List<Cafe>> getAllCafes() {
        return ResponseEntity.ok(cafeService.getAllCafes());
    }

    @Operation(summary = "Get cafe by ID", description = "Retrieves details of a cafe by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cafe> getCafeById(@PathVariable Long id) {
        return ResponseEntity.ok(cafeService.getCafeById(id));
    }

    @Operation(summary = "Create a new cafe", description = "Adds a new cafe to the system")
    @PostMapping
    public ResponseEntity<Cafe> createCafe(@RequestBody @Valid CafeCreateOrUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeService.createCafe(dto));
    }

    @Operation(summary = "Update a cafe", description = "Updates an existing cafe by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<Cafe> updateCafe(@PathVariable Long id, @RequestBody @Valid CafeCreateOrUpdateDto dto) {
        return ResponseEntity.ok(cafeService.updateCafe(id, dto));
    }

    @Operation(summary = "Delete a cafe", description = "Deletes a cafe by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCafe(@PathVariable Long id) {
        cafeService.deleteCafe(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a pizza to a cafe", description = "Associates a pizza with a specific cafe")
    @PostMapping("/{id}/pizzas")
    public ResponseEntity<Void> addPizzaToCafe(@PathVariable(name = "id") Long cafeId, @RequestBody @Valid PizzaCreateOrUpdateDto dto) {
        cafeService.addPizzaToCafe(cafeId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove a pizza from a cafe", description = "Removes a pizza from a specific cafe")
    @DeleteMapping("/{cafeId}/pizzas/{pizzaId}")
    public ResponseEntity<Void> deletePizzaFromCafe(@PathVariable Long cafeId, @PathVariable Long pizzaId) {
        cafeService.deletePizzaFromCafe(cafeId, pizzaId);
        return ResponseEntity.noContent().build();
    }

}
