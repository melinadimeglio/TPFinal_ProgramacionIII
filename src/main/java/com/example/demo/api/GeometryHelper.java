package com.example.demo.api;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeometryHelper {

    @Named("extractLat")
    public static Double extractLat(Geometry geometry) {

        if (geometry == null){
            return null;
        }

        List<Double> coordinates = geometry.getCoordinates();
        if (coordinates.size() > 1){
            return coordinates.get(1);
        }

        return null;
    }

    @Named("extractLon")
    public static Double extractLon(Geometry geometry) {

        if (geometry == null){
            return null;
        }

        List<Double> coordinates = geometry.getCoordinates();
        if (coordinates.size() > 0){
            return coordinates.get(0);
        }

        return null;

    }

}
