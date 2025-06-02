package com.example.demo.DTOs.Company.Response;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;
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
    private String taxId;
    private String location;
    private String phone;
    private boolean active;
    private List<ActivityResumeDTO> activities;

}
