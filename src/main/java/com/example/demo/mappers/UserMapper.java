package com.example.demo.mappers;

import com.example.demo.DTOs.User.UserCreateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDTO(UserEntity user);
    UserEntity toUserEntity (UserResponse userDTO);
    UserCreateDTO toResponseDTO(UserEntity user);
    void updateUserEntityFromDTO (UserUpdateDTO dto, @MappingTarget UserEntity user);

}
