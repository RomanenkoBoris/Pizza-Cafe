package com.pizza.pizzaproject.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.repository.UserRepository;
import com.pizza.pizzaproject.service.UserService;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        UserCreateOrUpdateDTO adminDto = new UserCreateOrUpdateDTO("admin", "admin","admin@example.com", "admin","password", "role_admin");
        UserCreateOrUpdateDTO userDto = new UserCreateOrUpdateDTO("user", "user","user@example.com", "user","password", "role_user");

        String responseAdmin = mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(adminDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        admin = objectMapper.readValue(responseAdmin, User.class);

        String responseUser = mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        user = objectMapper.readValue(responseUser, User.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        String response = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Map<String, Object>> users = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).get("username"));
        assertEquals("user", users.get(1).get("username"));
    }

    @Test
    void shouldDenyAccessToReturnAllUsersWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyAccessToReturnAllUsersForNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldGetUserByIdAsAdmin() throws Exception {
        String response = mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User existingUser = objectMapper.readValue(response, User.class);
        assertEquals(user.getId(), existingUser.getId());
        assertEquals(user.getUsername(), existingUser.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldGetOwnUserDetails() throws Exception {
        String response = mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User existingUser = objectMapper.readValue(response, User.class);
        assertEquals(user.getId(), existingUser.getId());
        assertEquals(user.getUsername(), existingUser.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldNotGetOtherUserDetails() throws Exception {
        mockMvc.perform(get("/api/users/{id}", admin.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotGetUserByIdWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/{id}", admin.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateOrUpdateDTO otherUserDto = new UserCreateOrUpdateDTO("otherUser", "otherUser","otheruser@example.com", "otherUser","password", "role_user");

        String response = mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(otherUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User otherUser = objectMapper.readValue(response, User.class);

        assertEquals(3, userRepository.count());
        assertEquals("otherUser", otherUser.getUsername());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateUserAsAdmin() throws Exception {
        UserCreateOrUpdateDTO updatedUserDto = new UserCreateOrUpdateDTO("updatedUser", "updatedUser","updatedUser@example.com", "user","password", "role_user");

        String response = mockMvc.perform(put("/api/users/{userName}", user.getUsername())
                        .content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User updatedUser = objectMapper.readValue(response, User.class);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("updatedUser", updatedUser.getFirst_name());
        assertEquals("updatedUser@example.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldUpdateOwnUser() throws Exception {
        UserCreateOrUpdateDTO updatedUserDto = new UserCreateOrUpdateDTO("updatedUser", "updatedUser","updatedUser@example.com", "user","password", "role_user");

        String response = mockMvc.perform(put("/api/users/{userName}", user.getUsername())
                        .content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User updatedUser = objectMapper.readValue(response, User.class);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("updatedUser", updatedUser.getFirst_name());
        assertEquals("updatedUser@example.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldNotUpdateOtherUser() throws Exception {
        UserCreateOrUpdateDTO updatedUserDto = new UserCreateOrUpdateDTO("updatedUser", "updatedUser","updatedUser@example.com", "user","password", "role_user");

        mockMvc.perform(put("/api/users/{userName}", admin.getUsername())
                        .content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDeleteOwnAccount() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldNotDeleteOtherUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", admin.getId()))
                .andExpect(status().isForbidden());
    }
}
