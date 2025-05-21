package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.ActivityResumeDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyCreateDTO {

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String tax_id;

    @NotBlank
    private String location;

    @NotBlank
    private String phone;

    private boolean active;

    private List<ActivityResumeDTO> activities;

}
