package com.example.demo.mappers;

import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
    TripResponseDTO toDTO(TripEntity entity);

    List <TripResponseDTO> toDTOList(List<TripEntity> entities);

    TripResumeDTO toResumeDTO(TripEntity entity);

    @Mapping(target = "users", ignore = true)
    TripEntity toEntity(TripCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TripUpdateDTO dto, @MappingTarget TripEntity entity);

    @AfterMapping
    default List<Long> mapUsersToIds(Set<UserEntity> users) {
        if (users == null) return null;
        return users.stream().map(UserEntity::getId).toList();
    }
}

