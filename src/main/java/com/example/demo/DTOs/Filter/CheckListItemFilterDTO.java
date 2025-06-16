package com.example.demo.DTOs.Filter;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemFilterDTO {
    private Long checklistId;
    private Boolean status;
}
