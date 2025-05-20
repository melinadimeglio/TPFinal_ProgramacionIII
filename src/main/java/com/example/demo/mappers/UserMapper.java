package com.example.demo.mappers;

import com.example.demo.DTOs.User.UserDTO;
import com.example.demo.DTOs.User.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserEntity user);
    UserEntity toUserEntity (UserDTO userDTO);
    UserResponseDTO toResponseDTO(UserEntity user);
    void updateUserEntityFromDTO (UserUpdateDTO dto, @MappingTarget UserEntity user);

}
