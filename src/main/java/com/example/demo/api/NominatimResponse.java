package com.example.demo.api;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class NominatimResponse {

    private String lat;
    private String lon;

}
