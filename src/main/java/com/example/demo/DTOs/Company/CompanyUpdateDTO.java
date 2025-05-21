package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.ActivityResumeDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyUpdateDTO {

    private String username;
    private String email;
    private String password;  // opcional para cambio de contraseña
    private String taxId;
    private String location;
    private String phone;
    private Boolean active;
    private List<ActivityResumeDTO> activities;

}
