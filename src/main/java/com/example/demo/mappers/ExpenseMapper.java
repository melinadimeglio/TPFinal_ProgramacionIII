package com.example.demo.mappers;

import com.example.demo.DTOs.Expense.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "user.id", target = "userId")
    ExpenseResponseDTO toDTO(ExpenseEntity entity);

    List<ExpenseResponseDTO> toDTOList(List<ExpenseEntity> entities);

    @Mapping(source = "userId", target = "user.id")
    ExpenseEntity toEntity(ExpenseCreateDTO dto);

    void updateEntityFromDTO(ExpenseUpdateDTO dto, @MappingTarget ExpenseEntity entity);
}
