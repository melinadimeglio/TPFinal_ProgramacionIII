package com.example.demo.mappers;

import com.example.demo.DTOs.Activity.*;
import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.entities.ActivityEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface ActivityMapper {

        @Mapping(target = "itinerary.id", source = "itineraryId")
        @Mapping(target = "users", ignore = true)
        @Mapping(target = "company", ignore = true)
        ActivityEntity toEntity(UserActivityCreateDTO dto);

        @Mapping(target = "company.id", source = "companyId")
        @Mapping(target = "itinerary", ignore = true)
        @Mapping(target = "users", ignore = true)
        ActivityEntity toEntity(CompanyActivityCreateDTO dto);

        @Mapping(target = "itineraryId", source = "itinerary.id")
        @Mapping(target = "companyId", source = "company.id")
        @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
        ActivityResponseDTO toDTO(ActivityEntity entity);

        List<ActivityResponseDTO> toDTOList(List<ActivityEntity> entities);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateEntityFromDTO(ActivityUpdateDTO dto, @MappingTarget ActivityEntity entity);

        default Set<Long> mapUsersToIds(Set<com.example.demo.entities.UserEntity> users) {
                return users == null ? null :
                        users.stream().map(com.example.demo.entities.UserEntity::getId).collect(Collectors.toSet());
                }
        }

