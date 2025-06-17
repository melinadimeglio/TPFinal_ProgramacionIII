package com.example.demo.mappers;

import com.example.demo.DTOs.CheckList.*;
import com.example.demo.DTOs.CheckList.Request.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.entities.CheckListItemEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CheckListItemMapper {

        @Mapping(source = "checklist.id", target = "checklistId")
        @Mapping(source = "checklist.user.id", target = "userId")
        CheckListItemResponseDTO toDTO(CheckListItemEntity entity);

        List<CheckListItemResponseDTO> toDTOList(List<CheckListItemEntity> entities);

        @Mapping(target = "checklist", ignore = true)
        CheckListItemEntity toEntity(CheckListItemCreateDTO dto);

        @Mapping(target = "checklist", ignore = true)
        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        void updateEntityFromDTO(CheckListItemUpdateDTO dto, @MappingTarget CheckListItemEntity entity);
}



