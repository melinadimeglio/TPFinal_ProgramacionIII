package com.example.demo.controllers;

import com.example.demo.DTOs.Dashboard.DashboardDTO;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Operations related to the dashboard")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAuthority('VER_DASHBOARD')")
    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard (
            @AuthenticationPrincipal CredentialEntity credential
            ){

        String email = credential.getEmail();

        DashboardDTO dashboardDTO = dashboardService.getDashboard(email);

        return ResponseEntity.ok(dashboardDTO);
    }
}
