package com.pizza.pizzaproject.mapper;

import com.pizza.pizzaproject.dto.PizzaCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import com.pizza.pizzaproject.entity.Pizza;
import com.pizza.pizzaproject.entity.PizzaSize;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    @Mapping(source = "size", target = "size", qualifiedByName = "stringToSize")
    @Mapping(target = "cafe", expression = "java(cafe)")
    Pizza pizzaFromDto(PizzaCreateOrUpdateDto dto, @Context Cafe cafe);

    @Named(value = "stringToSize")
    default PizzaSize stringToSize(String size) {
        try {
            return PizzaSize.valueOf(size.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Pizza size is incorrect. There are 3 available sizes: small, medium and large.");
        }
    }

}
