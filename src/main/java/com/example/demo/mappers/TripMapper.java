package com.example.demo.mappers;

import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResumeDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.DTOs.User.Response.UserResumeDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "users", expression = "java(mapUsersToDTO(entity.getUsers()))")
    TripResponseDTO toDTO(TripEntity entity);

    @AfterMapping
    default List<UserResumeDTO> mapUsersToDTO(Set<UserEntity> users) {
        if (users == null) return null;
        return users.stream()
                .map(u -> new UserResumeDTO(u.getId(), u.getUsername()))
                .toList();
    }

    List<TripResponseDTO> toDTOList(List<TripEntity> entities);

    TripResumeDTO toResumeDTO(TripEntity entity);

    @Mapping(target = "users", ignore = true)
    TripEntity toEntity(TripCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDTO(TripUpdateDTO dto, @MappingTarget TripEntity entity);

    @AfterMapping
    default List<Long> mapUsersToIds(Set<UserEntity> users) {
        if (users == null) return null;
        return users.stream().map(UserEntity::getId).toList();
    }
}

