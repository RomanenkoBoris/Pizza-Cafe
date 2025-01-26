package com.pizza.pizzaproject.service;

import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.mapper.CaffeMapper;
import com.pizza.pizzaproject.mapper.PizzaMapper;
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

    @Autowired
    private CaffeMapper cafeMapper;

    @Autowired
    private PizzaMapper pizzaMapper;

    public List<Cafe> getAllCafes() {
        return cafeRepository.findAll();
    }

    public Cafe getCafeById(Long id) {
        return findCafe(id);
    }

    public Cafe createCafe(CafeCreateOrUpdateDto dto) {
        return cafeRepository.save(cafeMapper.cafeFromDto(dto));
    }

    public Cafe updateCafe(Long id, CafeCreateOrUpdateDto dto) {
        Cafe existingCafe = findCafe(id);
        existingCafe.setName(dto.name());
        existingCafe.setLocation(dto.location());
        existingCafe.setPhone(dto.phone());
        return cafeRepository.save(existingCafe);
    }


    public void deleteCafe(Long id) {
        if (!cafeRepository.existsById(id))
            throw new EntityNotFoundException("Cafe with ID " + id + " not exists");
        cafeRepository.deleteById(id);
    }

    public void addPizzaToCafe(Long cafeId, PizzaCreateOrUpdateDto dto) {
        Cafe cafe = findCafe(cafeId);
        Pizza pizza = pizzaMapper.pizzaFromDto(dto, cafe);
        cafe.addPizza(pizza);
        cafeRepository.save(cafe);
    }

    public void deletePizzaFromCafe(Long cafeId, Long pizzaId) {
        Cafe cafe = findCafe(cafeId);
        Pizza pizza = findPizza(pizzaId);
        cafe.removePizza(pizzaId);
        cafeRepository.save(cafe);
        pizzaRepository.save(pizza);
    }

    private Pizza findPizza(Long id) {
        return pizzaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pizza with ID " + id + " not found"));
    }

    private Cafe findCafe(Long id) {
        return cafeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cafe with ID " + id + " not found"));
    }
}
