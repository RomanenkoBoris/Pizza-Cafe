package com.pizza.pizzaproject.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.mapper.CaffeMapper;
import com.pizza.pizzaproject.mapper.PizzaMapper;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.repository.PizzaRepository;
import com.pizza.pizzaproject.service.CafeService;
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
class CafeServiceTest {

    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private CaffeMapper cafeMapper;
    @Mock
    private PizzaMapper pizzaMapper;

    @InjectMocks
    private CafeService cafeService;

    private CafeCreateOrUpdateDto cafeDto;
    private PizzaCreateOrUpdateDto pizzaDto;

    @BeforeEach
    void setUp() {
        cafeDto = new CafeCreateOrUpdateDto("Cafe A", "Address AAAA", "11111111111");
        pizzaDto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), 1L);
    }

    @Test
    void getAllCafes_ShouldReturnCafeList() {
        List<Cafe> cafes = List.of(new Cafe(), new Cafe());
        when(cafeRepository.findAll()).thenReturn(cafes);
        assertEquals(2, cafeService.getAllCafes().size());
        verify(cafeRepository).findAll();
    }

    @Test
    void getCafeById_ShouldReturnCafe() {
        Long cafeId = 1L;
        Cafe cafe = new Cafe();
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));
        assertNotNull(cafeService.getCafeById(cafeId));
        verify(cafeRepository).findById(cafeId);
    }

    @Test
    void createCafe_ShouldSaveCafe() {
        Cafe cafe = new Cafe();
        when(cafeMapper.cafeFromDto(cafeDto)).thenReturn(cafe);
        when(cafeRepository.save(cafe)).thenReturn(cafe);
        assertNotNull(cafeService.createCafe(cafeDto));
        verify(cafeRepository).save(cafe);
    }

    @Test
    void updateCafe_ShouldUpdateExistingCafe() {
        Long cafeId = 1L;
        Cafe existingCafe = new Cafe();
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(existingCafe));
        when(cafeRepository.save(existingCafe)).thenReturn(existingCafe);
        assertNotNull(cafeService.updateCafe(cafeId, cafeDto));
        verify(cafeRepository).save(existingCafe);
    }

    @Test
    void deleteCafe_ShouldRemoveCafe() {
        Long cafeId = 1L;
        when(cafeRepository.existsById(cafeId)).thenReturn(true);
        cafeService.deleteCafe(cafeId);
        verify(cafeRepository).deleteById(cafeId);
    }

    @Test
    void addPizzaToCafe_ShouldAddPizza() {
        Long cafeId = 1L;
        Cafe cafe = new Cafe();
        Pizza pizza = new Pizza();
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));
        when(pizzaMapper.pizzaFromDto(pizzaDto, cafe)).thenReturn(pizza);
        cafeService.addPizzaToCafe(cafeId, pizzaDto);
        verify(cafeRepository).save(cafe);
    }

    @Test
    void deletePizzaFromCafe_ShouldRemovePizza() {
        Long cafeId = 1L;
        Long pizzaId = 1L;
        Cafe cafe = new Cafe();
        Pizza pizza = new Pizza();
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(pizza));
        cafeService.deletePizzaFromCafe(cafeId, pizzaId);
        verify(cafeRepository).save(cafe);
    }

}
