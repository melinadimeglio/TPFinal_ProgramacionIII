package com.example.demo.mapper;

import com.example.demo.DTO.UserDTO;
import com.example.demo.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserEntity user);
    UserEntity toUserEntity (UserDTO userDTO);

}
