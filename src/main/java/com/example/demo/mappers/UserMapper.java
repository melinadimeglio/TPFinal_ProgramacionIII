package com.example.demo.mappers;

import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponse;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = TripMapper.class)
public interface UserMapper {

    UserResponse toDTO(UserEntity user);
    UserEntity toUserEntity (UserCreateDTO userDTO);

    List<UserResponse> toDTOList(List<UserEntity> entities);

    UserCreateDTO toResponseDTO(UserEntity user);
    void updateUserEntityFromDTO (UserUpdateDTO dto, @MappingTarget UserEntity user);

}
