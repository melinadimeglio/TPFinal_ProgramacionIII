package com.example.demo.api;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class GeometryHelper {

    @Named("extractLat")
    public static Double extractLat(Geometry geometry) {
        return geometry != null && geometry.getCoordinates().size() > 1 ? geometry.getCoordinates().get(1) : null;
    }

    @Named("extractLon")
    public static Double extractLon(Geometry geometry) {
        return geometry != null && geometry.getCoordinates().size() > 0 ? geometry.getCoordinates().get(0) : null;
    }

}
