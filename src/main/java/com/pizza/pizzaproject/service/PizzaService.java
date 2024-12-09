package com.pizza.pizzaproject.service;

import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.repository.PizzaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzaService {

    @Autowired
    private PizzaRepository pizzaRepository;
    @Autowired
    private CafeRepository cafeRepository;

    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    public Pizza getPizzaById(Long id) {
        return findPizza(id);
    }

    public Pizza createPizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    public Pizza updatePizza(Long id, Pizza updatedPizza) {
        Pizza existingPizza = findPizza(id);
        if (updatedPizza.getCafe() != null && updatedPizza.getCafe().getId() != null) {
            Cafe cafe = findCafe(updatedPizza.getCafe().getId());
            existingPizza.setCafe(cafe);
        }
        existingPizza.setSize(updatedPizza.getSize());
        existingPizza.setName(updatedPizza.getName());
        existingPizza.setKeyIngredients(updatedPizza.getKeyIngredients());

        return pizzaRepository.save(existingPizza);
    }

    public void deletePizza(Long id){
        if (!pizzaRepository.existsById(id)){
            throw new EntityNotFoundException("Pizza with ID " + id + " not exists");
        }
        pizzaRepository.deleteById(id);
    }

    private Pizza findPizza(Long id){
        return pizzaRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Pizza with ID " + id + " not found"));
    }

    private Cafe findCafe(Long id){
        return cafeRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Cafe with ID " + id + " not found"));
    }
}
