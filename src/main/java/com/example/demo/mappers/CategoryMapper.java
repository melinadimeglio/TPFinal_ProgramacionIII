package com.example.demo.mappers;

import com.example.demo.DTOs.CategoryDTO;
import com.example.demo.api.GeometryHelper;
import com.example.demo.entities.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO (CategoryEntity category);

}
