package com.example.demo.DTO.CheckList;

import com.example.demo.entities.CheckListItemEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListDTO {

    private Long id;
    private String item;
    private Long tripId;
    private List<CheckListItemEntity> items;

}
