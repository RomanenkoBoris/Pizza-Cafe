package com.pizza.pizzaproject.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.mapper.PizzaMapper;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.repository.PizzaRepository;
import com.pizza.pizzaproject.service.PizzaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private CafeRepository cafeRepository;

    @Mock
    private PizzaMapper pizzaMapper;

    @InjectMocks
    private PizzaService pizzaService;

    private PizzaCreateOrUpdateDto pizzaDto;
    private Cafe cafe;
    private Pizza pizza;

    @BeforeEach
    void setUp() {
        pizzaDto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), 1L);
        cafe = new Cafe();
        pizza = new Pizza();
    }

    @Test
    void getAllPizzas_ShouldReturnPizzaList() {
        List<Pizza> pizzas = List.of(new Pizza(), new Pizza());
        when(pizzaRepository.findAll()).thenReturn(pizzas);
        assertEquals(2, pizzaService.getAllPizzas().size());
        verify(pizzaRepository).findAll();
    }

    @Test
    void getPizzaById_ShouldReturnPizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(pizza));
        assertNotNull(pizzaService.getPizzaById(pizzaId));
        verify(pizzaRepository).findById(pizzaId);
    }

    @Test
    void createPizza_ShouldSavePizza() {
        when(cafeRepository.findById(pizzaDto.cafeId())).thenReturn(Optional.of(cafe));
        when(pizzaMapper.pizzaFromDto(pizzaDto, cafe)).thenReturn(pizza);
        when(pizzaRepository.save(pizza)).thenReturn(pizza);
        assertNotNull(pizzaService.createPizza(pizzaDto));
        verify(pizzaRepository).save(pizza);
    }

    @Test
    void updatePizza_ShouldUpdateExistingPizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(pizza));
        when(cafeRepository.findById(pizzaDto.cafeId())).thenReturn(Optional.of(cafe));
        when(pizzaMapper.pizzaFromDto(pizzaDto, cafe)).thenReturn(pizza);
        when(pizzaRepository.save(pizza)).thenReturn(pizza);
        assertNotNull(pizzaService.updatePizza(pizzaId, pizzaDto));
        verify(pizzaRepository).save(pizza);
    }

    @Test
    void deletePizza_ShouldRemovePizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.existsById(pizzaId)).thenReturn(true);
        pizzaService.deletePizza(pizzaId);
        verify(pizzaRepository).deleteById(pizzaId);
    }
}
