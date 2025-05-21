package com.example.demo.mappers;

import com.example.demo.DTOs.Activity.ActivityCreateDTO;
import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityResumeDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.entities.ActivityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(source = "itinerary.id", target = "itineraryId")
    @Mapping(source = "user.id", target = "userId")
    ActivityResponseDTO toDTO(ActivityEntity entity);

    List<ActivityResponseDTO> toDTOList(List<ActivityEntity> entities);

    @Mapping(source = "itineraryId", target = "itinerary.id")
    @Mapping(source = "userId", target = "user.id")
    ActivityEntity toEntity(ActivityCreateDTO dto);

    void updateEntityFromDTO(ActivityUpdateDTO dto, @MappingTarget ActivityEntity entity);

    @Mapping(source = "description", target = "description")
    ActivityResumeDTO toResumeDTO(ActivityEntity entity);

    List<ActivityResumeDTO> toResumeDTOList(List<ActivityEntity> entities);
}
