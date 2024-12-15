package com.pizza.pizzaproject.mapper;

import com.pizza.pizzaproject.dto.CafeCreateOrUpdateDto;
import com.pizza.pizzaproject.entity.Cafe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaffeMapper {

    Cafe cafeFromDto(CafeCreateOrUpdateDto dto);
}
