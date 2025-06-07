package com.example.demo.mappers;

import com.example.demo.DTOs.Itinerary.Request.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.entities.ItineraryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ActivityMapper.class})
public interface ItineraryMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "trip.id", target = "tripId")
    ItineraryResponseDTO toDTO(ItineraryEntity entity);

    List<ItineraryResponseDTO> toDTOList(List<ItineraryEntity> entities);

    @Mapping(source = "tripId", target = "trip.id")
    ItineraryEntity toEntity(ItineraryCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ItineraryUpdateDTO dto, @MappingTarget ItineraryEntity entity);
}
