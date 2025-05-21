package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.ActivityResumeDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String tax_id;
    private String location;
    private String phone;
    private boolean active;
    private List<ActivityResumeDTO> activities;

}
