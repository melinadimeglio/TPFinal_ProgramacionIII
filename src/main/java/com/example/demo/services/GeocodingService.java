package com.example.demo.services;

import com.example.demo.api.Coordinates;
import com.example.demo.api.NominatimResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Coordinates getCoordinates (String destino){
        String url = "https://nominatim.openstreetmap.org/search?q=" + UriUtils.encode(destino, StandardCharsets.UTF_8)
                + "&format=json&limit=2";

        ResponseEntity<NominatimResponse[]> response = restTemplate.getForEntity(url, NominatimResponse[].class);
        NominatimResponse[] result = response.getBody();

        if (result != null && result.length > 0){
            double lat = Double.parseDouble(result[0].getLat());
            double lon = Double.parseDouble(result[0].getLon());
            //double lat = Double.parseDouble(result[0].getLat());
            //double lon = Double.parseDouble(result[1].getLon());
            return new Coordinates(lat, lon);
        }

        throw new RuntimeException("No se encontraron coordinadas para el destino ingresado.");

    }

}
