package com.example.demo.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class CoordinatesDTO {

    private double lat;
    private double lon;
    private String name;

}
