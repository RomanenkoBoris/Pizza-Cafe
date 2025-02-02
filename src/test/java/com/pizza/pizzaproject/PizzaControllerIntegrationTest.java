package com.pizza.pizzaproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.repository.PizzaRepository;
import com.pizza.pizzaproject.service.PizzaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser
public class PizzaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cafe testCafe;

    private Pizza testPizza;


    @BeforeEach
    void setUp() throws Exception {
        CafeCreateOrUpdateDto cafeDto = new CafeCreateOrUpdateDto("Cafe A", "Address AAAA", "11111111111");
        String cafeResponse = mockMvc.perform(post("/api/cafes")
                        .with(user("admin").roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(cafeDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testCafe = objectMapper.readValue(cafeResponse, Cafe.class);

        PizzaCreateOrUpdateDto pizzaDto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafe.getId());
        String pizzaResponse = mockMvc.perform(post("/api/pizzas")
                        .with(user("admin").roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(pizzaDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testPizza = objectMapper.readValue(pizzaResponse, Pizza.class);
    }

    @AfterEach
    void tearDown() {
        pizzaRepository.deleteAll();
        cafeRepository.deleteAll();
    }

    @Test
    void shouldReturnAllPizzas() throws Exception {
        String response = mockMvc.perform(get("/api/pizzas"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Map<String, Object>> pizzas = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, pizzas.size());
        assertEquals("Pizza A", pizzas.get(0).get("name"));
    }

    @Test
    void shouldReturnPizzaById() throws Exception {
        String response = mockMvc.perform(get("/api/pizzas/{id}", testPizza.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pizza existingPizza = objectMapper.readValue(response, Pizza.class);
        assertEquals(testPizza.getId(), existingPizza.getId());
        assertEquals(testPizza.getName(), existingPizza.getName());
    }
//
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateNewPizza() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza C", "Small", "Mushrooms, Olives", new BigDecimal("8.99"), testCafe.getId());

        String response = mockMvc.perform(post("/api/pizzas")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pizza createdPizza = objectMapper.readValue(response, Pizza.class);
        assertEquals("Pizza C", createdPizza.getName());
        assertEquals(testCafe.getId(), createdPizza.getCafe().getId());
        assertTrue(pizzaRepository.existsById(createdPizza.getId()));

        List<Pizza> pizzas = pizzaRepository.findAll();
        assertEquals(2, pizzas.size());
    }

    @Test
    void shouldDenyAccessToCreatePizzaWhenNotAuthenticated() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza D", "Medium", "Cheese, Tomato", new BigDecimal("9.99"), testCafe.getId());

        mockMvc.perform(post("/api/pizzas")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyAccessToCreatePizzaForNonAdminUser() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza D", "Medium", "Cheese, Tomato", new BigDecimal("9.99"), testCafe.getId());

        mockMvc.perform(post("/api/pizzas")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateExistingPizza() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Updated Pizza", "Large", "Cheese, Ham", new BigDecimal("11.99"), testCafe.getId());

        String response = mockMvc.perform(put("/api/pizzas/{id}", testPizza.getId())
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pizza updatedPizza = objectMapper.readValue(response, Pizza.class);
        assertEquals(testPizza.getId(), updatedPizza.getId());
        assertEquals("Updated Pizza", updatedPizza.getName());
        List<Pizza> pizzas = pizzaRepository.findAll();
        assertEquals(1, pizzas.size());
        assertEquals("Updated Pizza", pizzas.get(0).getName());
    }

    @Test
    void shouldDenyAccessToUpdateExistingPizzaWhenNotAuthenticated() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Updated Pizza", "Large", "Cheese, Ham", new BigDecimal("11.99"), testCafe.getId());

        mockMvc.perform(put("/api/pizzas/{id}", testPizza.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyAccessToUpdateExistingPizzaForNonAdminUser() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Updated Pizza", "Large", "Cheese, Ham", new BigDecimal("11.99"), testCafe.getId());

        mockMvc.perform(put("/api/pizzas/{id}", testPizza.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeletePizza() throws Exception {
        mockMvc.perform(delete("/api/pizzas/{id}", testPizza.getId()))
                .andExpect(status().isNoContent());

        assert pizzaRepository.findById(testPizza.getId()).isEmpty();
    }

    @Test
    void shouldDenyAccessToDeletePizzaWhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/pizzas/{id}", testPizza.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyAccessToDeletePizzaForNonAdminUser() throws Exception {
        mockMvc.perform(delete("/api/pizzas/{id}", testPizza.getId()))
                .andExpect(status().isForbidden());
    }
}

