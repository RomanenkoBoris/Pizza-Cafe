package com.pizza.pizzaproject.mapper;

import com.pizza.pizzaproject.dto.UserCreateOrUpdateDTO;
import com.pizza.pizzaproject.entity.User;
import com.pizza.pizzaproject.enums.UserRole;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToUserRole")
    @Mapping(target = "password", expression = "java(password)")
    User userFromDto(UserCreateOrUpdateDTO dto, @Context String password);

    @Named(value = "stringToUserRole")
    default UserRole stringToUserRole (String role){
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("User role is incorrect. There are 2 available roles: role_user, role_admin");
        }
    }
}
