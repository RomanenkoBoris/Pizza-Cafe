package com.pizza.pizzaproject.pizza;

import com.pizza.pizzaproject.cafe.Cafe;
import com.pizza.pizzaproject.cafe.CafeRepository;
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
        return pizzaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pizza with ID " + id + " not found"));
    }

    public Pizza createPizza(Pizza pizza) {
        validatePizza(pizza);
        return pizzaRepository.save(pizza);
    }

    public Pizza updatePizza(Long id, Pizza updatedPizza) {
        Pizza existingPizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pizza with ID " + id + " not found"));
        if (updatedPizza.getCafe() != null && updatedPizza.getCafe().getId() != null) {
            Cafe cafe = cafeRepository.findById(updatedPizza.getCafe().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe with ID " + updatedPizza.getCafe().getId() + " not found"));
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

    private void validatePizza(Pizza pizza){
        if (pizza == null)
            throw new IllegalArgumentException("Pizza cannot ba null");
    }
}
