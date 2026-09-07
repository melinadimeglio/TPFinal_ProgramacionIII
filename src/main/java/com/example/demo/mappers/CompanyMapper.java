package com.example.demo.mappers;

import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.entities.CompanyEntity;
import org.mapstruct.*;

import java.util.List;
@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "email", source = "credential.email")
    CompanyResponseDTO toDTO(CompanyEntity company);

    CompanyEntity toEntity(CompanyCreateDTO dto);

    List<CompanyResponseDTO> toDTOList(List<CompanyEntity> companies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "credential", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompanyEntityFromDTO(CompanyUpdateDTO dto, @MappingTarget CompanyEntity entity);
}
