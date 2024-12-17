package com.pizza.pizzaproject.service;

import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.mapper.UserMapper;
import com.pizza.pizzaproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper mapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    public User createUser(@Valid UserCreateOrUpdateDTO dto) {
        String codedPassword = passwordEncoder.encode(dto.password());
        User user = mapper.userFromDto(dto, codedPassword);
        return userRepository.save(user);
    }

    public User updateUser(String userName, @Valid UserCreateOrUpdateDTO dto) {
        User user = userRepository.getUserByUsername(userName);
        if (user == null)
            throw new IllegalArgumentException("User with username " + userName + " not exists");
        user.setFirst_name(dto.first_name());
        user.setSecond_name(dto.second_name());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User with ID " + id + " not exists");
        userRepository.deleteById(id);
    }
}
