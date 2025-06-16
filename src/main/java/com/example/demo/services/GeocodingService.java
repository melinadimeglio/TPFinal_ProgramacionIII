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

            return new Coordinates(lat, lon);
        } else if (result == null || result.length == 0) {
            switch (destino.toLowerCase()) {
                case "argentina":
                    return new Coordinates(-38.4161, -63.6167);
                case "mar del plata":
                    return new Coordinates(-38.0055, -57.5426);
                case "buenos aires":
                    return new Coordinates(-34.6037, -58.3816);
            }
            throw new RuntimeException("No coordinates were found for the entered destination.");
        }

        throw new RuntimeException("No coordinates were found for the entered destination.");

    }

}
