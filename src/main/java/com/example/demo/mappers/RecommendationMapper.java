package com.example.demo.mappers;

import com.example.demo.api.Feature;
import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.api.GeometryHelper;
import com.example.demo.entities.CategoryEntity;
import com.example.demo.entities.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = GeometryHelper.class)
public interface RecommendationMapper {


    @Mapping(target = "lat", source = "geometry", qualifiedByName = "extractLat")
    @Mapping(target = "lon", source = "geometry", qualifiedByName = "extractLon")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(target = "categories", ignore = true)
    RecommendationDTO toDTO (Feature feature);

    @Mapping(target = "lat", source = "geometry", qualifiedByName = "extractLat")
    @Mapping(target = "lon", source = "geometry", qualifiedByName = "extractLon")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(target = "categories", ignore = true)
    RecommendationEntity toEntity (Feature feature);

    RecommendationDTO toDTO(RecommendationEntity entity);

}
