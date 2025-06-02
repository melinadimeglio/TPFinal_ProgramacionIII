package com.example.demo.mappers;

import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.entities.CompanyEntity;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {


    CompanyResponseDTO toDTO(CompanyEntity company);
    CompanyEntity toEntity(CompanyCreateDTO dto);

    List<CompanyResponseDTO> toDTOList(List<CompanyEntity> companies);

    void updateCompanyEntityFromDTO(CompanyUpdateDTO dto, @MappingTarget CompanyEntity entity);

}
