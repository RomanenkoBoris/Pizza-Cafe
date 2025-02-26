package com.pizza.pizzaproject.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.mapper.UserMapper;
import com.pizza.pizzaproject.repository.UserRepository;
import com.pizza.pizzaproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateOrUpdateDTO userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserCreateOrUpdateDTO("admin", "admin","admin@example.com", "admin","password", "role_admin");
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(2, userService.getAllUsers().size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertNotNull(userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }
    @Test
    void createUser_ShouldSaveUser() {
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userDto.password())).thenReturn(encodedPassword);
        User user = new User();
        when(userMapper.userFromDto(userDto, encodedPassword)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        assertNotNull(userService.createUser(userDto));
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateExistingUser() {
        String userName = "admin";
        User existingUser = new User();
        when(userRepository.getUserByUsername(userName)).thenReturn(existingUser);
        when(passwordEncoder.encode(userDto.password())).thenReturn("newEncodedPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        assertNotNull(userService.updateUser(userName, userDto));
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }
}