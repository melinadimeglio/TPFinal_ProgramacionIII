package com.example.demo.mappers;

import com.example.demo.DTOs.Company.CompanyCreateDTO;
import com.example.demo.DTOs.Company.CompanyResponseDTO;
import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.entities.CompanyEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activities", ignore = true)
    CompanyEntity toEntity(CompanyCreateDTO dto);

    CompanyResponseDTO toResponseDTO(CompanyEntity entity);

    List<CompanyResponseDTO> toResponseDTOList(List<CompanyEntity> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CompanyUpdateDTO dto, @MappingTarget CompanyEntity entity);
}
