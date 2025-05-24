package com.example.demo.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Feature {

    private Geometry geometry;
    private Properties properties;

}


