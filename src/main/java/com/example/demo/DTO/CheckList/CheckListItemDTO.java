package com.example.demo.DTO.CheckList;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListItemDTO {

    private Long id;
    private String description;
    private boolean status;
    private Long userId;
    private String userName;

}
