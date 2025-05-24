package com.example.demo.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Properties {

    private String name;
    private String kinds;
    private String wikidata;
    private String xid;

}
