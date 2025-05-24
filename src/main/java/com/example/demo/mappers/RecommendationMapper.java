package com.example.demo.mappers;

import com.example.demo.api.Feature;
import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.api.GeometryHelper;
import com.example.demo.entities.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = GeometryHelper.class)
public interface RecommendationMapper {

    @Mapping(target = "lat", source = "geometry", qualifiedByName = "extractLat")
    @Mapping(target = "lon", source = "geometry", qualifiedByName = "extractLon")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(expression = "java(feature.getProperties().getKinds())", target = "description")
    RecommendationDTO toDTO (Feature feature);

    @Mapping(target = "lat", source = "geometry", qualifiedByName = "extractLat")
    @Mapping(target = "lon", source = "geometry", qualifiedByName = "extractLon")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(expression = "java(feature.getProperties().getKinds())", target = "description")
    RecommendationEntity toEntity (Feature feature);

}
