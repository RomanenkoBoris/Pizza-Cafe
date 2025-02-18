package com.pizza.pizzaproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cafe testCafe;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnBadRequestForInvalidInput() throws Exception{
        CafeCreateOrUpdateDto cafeDto = new CafeCreateOrUpdateDto("CN", "LO", "11");

        mockMvc.perform(post("/api/cafes")
                        .content(objectMapper.writeValueAsString(cafeDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnNotFoundForNonExistingCafe() throws Exception{
        String response = mockMvc.perform(get("/api/cafes/{id}", 999999))
                .andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
        assertEquals("{\"EntityNotFoundException\":\"Cafe with ID 999999 not found\"}", response);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnExceptionForInvalidArgument() throws Exception{

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

        PizzaCreateOrUpdateDto pizzaDto = new PizzaCreateOrUpdateDto("Pizza C", "Little", "Mushrooms, Olives", new BigDecimal("8.99"), testCafe.getId());
        String response = mockMvc.perform(post("/api/pizzas")
                        .content(objectMapper.writeValueAsString(pizzaDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response);
        assertEquals("{\"IllegalArgumentException\":\"Pizza size is incorrect. There are 3 available sizes: small, medium and large.\"}", response);
    }
}
