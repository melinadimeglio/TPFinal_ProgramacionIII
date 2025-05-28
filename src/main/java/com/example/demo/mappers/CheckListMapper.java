package com.example.demo.mappers;


import com.example.demo.DTOs.CheckList.*;
import com.example.demo.entities.CheckListEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CheckListItemMapper.class})
    public interface CheckListMapper {

        @Mapping(source = "trip.id", target = "tripId")
        @Mapping(source = "user.id", target = "userId")
        CheckListResponseDTO toDTO(CheckListEntity entity);

        List<CheckListResponseDTO> toDTOList(List<CheckListEntity> entities);

        @Mapping(target = "trip", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "items", ignore = true)
        CheckListEntity toEntity(CheckListCreateDTO dto);

        @Mapping(target = "trip", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "items", ignore = true)
        void updateEntityFromDTO(CheckListUpdateDTO dto, @MappingTarget CheckListEntity entity);
    }

