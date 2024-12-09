package com.pizza.pizzaproject.service;

import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.repository.PizzaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CafeService {

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    public List<Cafe> getAllCafes(){
        return cafeRepository.findAll();
    }

    public Cafe getCafeById(Long id){
        return findCafe(id);
    }

    public Cafe createCafe(Cafe cafe){
        return cafeRepository.save(cafe);
    }

    public Cafe updateCafe(Long id, Cafe updatedCafe){
        Cafe existingCafe = findCafe(id);
        existingCafe.setName(updatedCafe.getName());
        existingCafe.setLocation(updatedCafe.getLocation());
        existingCafe.setPhone(updatedCafe.getPhone());
        return cafeRepository.save(existingCafe);
    }


    public void deleteCafe(Long id){
        if (!cafeRepository.existsById(id))
            throw new EntityNotFoundException("Cafe with ID " + id + " not exists");
        cafeRepository.deleteById(id);
    }

    public void addPizzaToCafe(Long cafeId, Pizza pizza){
        Cafe cafe = findCafe(cafeId);
        cafe.addPizza(pizza);
        cafeRepository.save(cafe);
        pizzaRepository.save(pizza);
    }

    public void deletePizzaFromCafe(Long cafeId, Long pizzaId){
        Cafe cafe = findCafe(cafeId);
        Pizza pizza = findPizza(pizzaId);
        cafe.removePizza(pizzaId);
        cafeRepository.save(cafe);
        pizzaRepository.save(pizza);
    }

    private Pizza findPizza(Long id){
        return pizzaRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Pizza with ID " + id + " not found"));
    }

    private Cafe findCafe(Long id){
        return cafeRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Cafe with ID " + id + " not found"));
    }
}
