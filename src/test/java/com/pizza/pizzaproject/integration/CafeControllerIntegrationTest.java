package com.pizza.pizzaproject.integration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.enums.PizzaSize;
import com.pizza.pizzaproject.mapper.PizzaMapper;
import com.pizza.pizzaproject.repository.CafeRepository;
import com.pizza.pizzaproject.repository.PizzaRepository;
import com.pizza.pizzaproject.service.CafeService;
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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser
public class CafeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private PizzaRepository pizzaRepository;


    @Autowired
    PizzaMapper pizzaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Cafe testCafeA;
    private Cafe testCafeB;



    @BeforeEach
    void setUp() throws Exception {
        CafeCreateOrUpdateDto cafeA = new CafeCreateOrUpdateDto("Cafe A", "Address AAAA", "11111111111");
        CafeCreateOrUpdateDto cafeB = new CafeCreateOrUpdateDto("Cafe B", "Address BBBB", "22222222222");

        String responseA = mockMvc.perform(post("/api/cafes")
                        .with(user("admin").roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(cafeA))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testCafeA = objectMapper.readValue(responseA, Cafe.class);

        String responseB = mockMvc.perform(post("/api/cafes")
                        .with(user("admin").roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(cafeB))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testCafeB = objectMapper.readValue(responseB, Cafe.class);
    }

    @AfterEach
    void tearDown(){
        cafeRepository.deleteAll();
    }

    @Test
    void shouldReturnAllCafes() throws Exception {
        String response = mockMvc.perform(get("/api/cafes"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Map<String, Object>> cafes = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, cafes.size());
        assertEquals("Cafe A", cafes.get(0).get("name"));
        assertEquals("Cafe B", cafes.get(1).get("name"));
    }

    @Test
    void shouldReturnCafeById() throws Exception{
        String response = mockMvc.perform(get("/api/cafes/{id}", testCafeA.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Cafe existingCafe = objectMapper.readValue(response, Cafe.class);
        assertEquals(testCafeA.getId(), existingCafe.getId());
        assertEquals(testCafeA.getName(), existingCafe.getName());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateNewCafe() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Cafe C", "Address CCCC", "33333333333");

        String response = mockMvc.perform(post("/api/cafes")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Cafe createdCafe = objectMapper.readValue(response, Cafe.class);

        assertEquals("Cafe C", createdCafe.getName());
        assertEquals("Address CCCC", createdCafe.getLocation());

        List<Cafe> cafes = cafeRepository.findAll();
        assertEquals(3, cafes.size());
        assertEquals("Cafe C", cafes.get(2).getName());
        assertEquals("Address CCCC", cafes.get(2).getLocation());
    }

    @Test
    void shouldDenyAccessToCreateCafeWhenNotAuthenticated() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Cafe C", "Address CCCC", "33333333333");

        mockMvc.perform(post("/api/cafes")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyAccessToCreateCafeForNonAdminUser() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Cafe C", "Address CCCC", "33333333333");

        mockMvc.perform(post("/api/cafes")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateExistingCafeAsAdmin() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Updated Cafe", "Updated Address", "0000000000000000");

        String response = mockMvc.perform(put("/api/cafes/{id}", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Cafe updatedCafe = objectMapper.readValue(response, Cafe.class);
        assertEquals(testCafeA.getId(), updatedCafe.getId());
        assertEquals(updatedCafe.getName(), "Updated Cafe");

        List<Cafe> cafes = cafeRepository.findAll();
        assertEquals(cafes.size(), 2);
        assertEquals(cafes.get(0).getLocation(), "Updated Address");

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldFailToUpdateCafeAsNonAdmin() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Updated Cafe", "Updated Address", "0000000000000000");

        mockMvc.perform(put("/api/cafes/{id}", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailToUpdateCafeWhenUnauthorized() throws Exception {
        CafeCreateOrUpdateDto dto = new CafeCreateOrUpdateDto("Updated Cafe", "Updated Address", "0000000000000000");

        mockMvc.perform(put("/api/cafes/{id}", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteCafeAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/cafes/{id}", testCafeB.getId()))
                .andExpect(status().isNoContent());

        assert cafeRepository.findById(testCafeB.getId()).isEmpty();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldFailToDeleteCafeAsNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/cafes/{id}", testCafeB.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailToDeleteCafeAsUnauthenticatedUser() throws Exception {
        mockMvc.perform(delete("/api/cafes/{id}", testCafeB.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAddPizzaToCafeAsAdmin() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA.getId());

        mockMvc.perform(post("/api/cafes/{id}/pizzas", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<Pizza> pizzas = pizzaRepository.findAll();
        assertEquals(pizzas.size(), 1);
        assertEquals(pizzas.get(0).getName(), "Pizza A");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldFailToAddPizzaToCafeAsNonAdmin() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA.getId());

        mockMvc.perform(post("/api/cafes/{id}/pizzas", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailToAddPizzaToCafeAsUnauthenticatedUser() throws Exception {
        PizzaCreateOrUpdateDto dto = new PizzaCreateOrUpdateDto("Pizza A", "medium", "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA.getId());

        mockMvc.perform(post("/api/cafes/{id}/pizzas", testCafeA.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeletePizzaFromCafeAsAdmin() throws Exception {
        Pizza pizza = pizzaRepository.save(new Pizza("Pizza A", PizzaSize.MEDIUM, "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA));

        mockMvc.perform(delete("/api/cafes/{cafeId}/pizzas/{pizzaId}", testCafeA.getId(), pizza.getId()))
                .andExpect(status().isNoContent());

        assertEquals(pizzaRepository.findAll().size(), 0);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldFailToDeletePizzaFromCafeAsNonAdmin() throws Exception {
        Pizza pizza = pizzaRepository.save(new Pizza("Pizza A", PizzaSize.MEDIUM, "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA));

        mockMvc.perform(delete("/api/cafes/{cafeId}/pizzas/{pizzaId}", testCafeA.getId(), pizza.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailToDeletePizzaFromCafeAsUnauthenticatedUser() throws Exception {
        Pizza pizza = pizzaRepository.save(new Pizza("Pizza A", PizzaSize.MEDIUM, "mushrooms A, cheese A, ham A", new BigDecimal("9.99"), testCafeA));

        mockMvc.perform(delete("/api/cafes/{cafeId}/pizzas/{pizzaId}", testCafeA.getId(), pizza.getId()))
                .andExpect(status().isForbidden());
    }

}
