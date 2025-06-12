package com.example.demo.mappers;

import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Activity.CompanyActivityUpdateDTO;
import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.Response.CompanyResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

        @Mapping(target = "id", ignore = true)
        //@Mapping(target = "itinerary.id", source = "itineraryId")
        @Mapping(target = "users", ignore = true)
        @Mapping(target = "company", ignore = true)
        ActivityEntity toEntity(UserActivityCreateDTO dto);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "company", source = "company")
        @Mapping(target = "price", source = "dto.price")
        @Mapping(target = "name", source = "dto.name")
        @Mapping(target = "description", source = "dto.description")
        @Mapping(target = "category", source = "dto.category")
        @Mapping(target = "date", source = "dto.date")
        @Mapping(target = "startTime", source = "dto.startTime")
        @Mapping(target = "endTime", source = "dto.endTime")
        @Mapping(target = "available_quantity", source = "dto.available_quantity")
        @Mapping(target = "itinerary", ignore = true)
        @Mapping(target = "users", ignore = true)
        ActivityEntity toEntity(CompanyActivityCreateDTO dto, CompanyEntity company);

        @Mapping(target = "itineraryId", source = "itinerary.id")
        @Mapping(target = "companyId", source = "company.id")
        @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
        ActivityResponseDTO toDTO(ActivityEntity entity);

        List<ActivityResponseDTO> toDTOList(List<ActivityEntity> entities);

        CompanyResponseDTO toCompanyResponseDTO(ActivityEntity entity);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateEntityFromDTO(ActivityUpdateDTO dto, @MappingTarget ActivityEntity entity);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateEntityFromCompanyDTO(CompanyActivityUpdateDTO dto, @MappingTarget ActivityEntity entity);

        default Set<Long> mapUsersToIds(Set<UserEntity> users) {
                return users == null ? null :
                        users.stream().map(UserEntity::getId).collect(Collectors.toSet());
        }
}
