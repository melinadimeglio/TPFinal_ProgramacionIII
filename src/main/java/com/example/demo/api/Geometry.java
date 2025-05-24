package com.example.demo.api;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Geometry {

    private List<Double> coordinates;

}
