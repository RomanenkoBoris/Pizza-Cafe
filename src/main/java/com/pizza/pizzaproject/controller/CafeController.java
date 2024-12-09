package com.pizza.pizzaproject.controller;

import com.pizza.pizzaproject.service.CafeService;
import com.pizza.pizzaproject.entity.Cafe;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    @Autowired
    private CafeService cafeService;

    @GetMapping
    public ResponseEntity<List<Cafe>> getAllCafes(){
        return ResponseEntity.ok(cafeService.getAllCafes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cafe> getCafeById(@PathVariable Long id){
        return ResponseEntity.ok(cafeService.getCafeById(id));
    }

    @PostMapping
    public ResponseEntity<Cafe> createCafe(@RequestBody @Valid Cafe cafe){
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeService.createCafe(cafe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cafe> updateCafe(@PathVariable Long id, @RequestBody @Valid Cafe cafe){
        return ResponseEntity.ok(cafeService.updateCafe(id, cafe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCafe(@PathVariable Long id){
        cafeService.deleteCafe(id);
        return ResponseEntity.noContent().build();
    }


}