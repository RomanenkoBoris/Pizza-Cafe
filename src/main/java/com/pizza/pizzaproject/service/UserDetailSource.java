package com.pizza.pizzaproject.service;

import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.repository.UserRepository;
import com.pizza.pizzaproject.security.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailSource implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User with username " + username + " not found");
        return new UserData(user);
    }
}
