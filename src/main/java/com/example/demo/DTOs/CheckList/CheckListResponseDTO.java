package com.example.demo.DTOs.CheckList;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListResponseDTO {

        private Long id;
        private String name;
        private boolean completed;
        private Long tripId;
        private Long userId;
        private List<CheckListItemResponseDTO> items;
    }
