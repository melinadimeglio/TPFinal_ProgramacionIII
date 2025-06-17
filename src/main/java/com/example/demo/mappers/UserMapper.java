package com.example.demo.mappers;

import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = TripMapper.class,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(source = "trips", target = "destinos")
    UserResponseDTO toDTO(UserEntity user);
    UserEntity toUserEntity (UserCreateDTO userDTO);

    List<UserResponseDTO> toDTOList(List<UserEntity> entities);

    UserCreateDTO toResponseDTO(UserEntity user);

    @Mapping(target = "id", ignore = true)
    void updateUserEntityFromDTO (UserUpdateDTO dto, @MappingTarget UserEntity user);

}
