package com.example.demo.mappers;

import com.example.demo.DTOs.Itinerary.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.entities.ItineraryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ActivityMapper.class})
public interface ItineraryMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "trip.id", target = "tripId")
    ItineraryResponseDTO toDTO(ItineraryEntity entity);

    List<ItineraryResponseDTO> toDTOList(List<ItineraryEntity> entities);

    // SIN @Mapping: user y trip los maneja el service
    ItineraryEntity toEntity(ItineraryCreateDTO dto);

    void updateEntityFromDTO(ItineraryUpdateDTO dto, @MappingTarget ItineraryEntity entity);
}
