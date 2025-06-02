package com.example.demo.DTOs.CheckList.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemResponseDTO {

    private Long id;
    private String description;
    private boolean status;
    private Long userId;
    private Long checklistId;
}