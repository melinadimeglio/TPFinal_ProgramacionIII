package com.example.demo.mappers;

import com.example.demo.DTOs.Activity.*;
import com.example.demo.entities.ActivityEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "itinerary.id", source = "itineraryId")
    ActivityEntity toEntity(UserActivityCreateDTO dto);

    @Mapping(target = "company.id", source = "companyId")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    ActivityEntity toEntity(CompanyActivityCreateDTO dto);

    @Mapping(target = "itineraryId", source = "itinerary.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "companyId", source = "company.id")
    ActivityResponseDTO toDTO(ActivityEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ActivityUpdateDTO dto, @MappingTarget ActivityEntity entity);
}
