package com.example.demo.controllers;

import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.controllers.hateoas.CompanyModelAssembler;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyModelAssembler assembler;

    @Autowired
    public CompanyController(CompanyService companyService, CompanyModelAssembler assembler) {
        this.companyService = companyService;
        this.assembler = assembler;
    }

    @Operation(summary = "Get all companies", description = "Returns a list of all companies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CompanyResponseDTO>>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(companies));
    }

    @Operation(summary = "Get company by ID", description = "Returns a specific company by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> getCompanyById(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.findById(id);
        return ResponseEntity.ok(assembler.toModel(company));
    }

    @Operation(summary = "Create a new company", description = "Creates a new company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EntityModel<CompanyResponseDTO>> createCompany(@RequestBody @Valid CompanyCreateDTO dto) {
        CompanyResponseDTO company = companyService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(company));
    }

    @Operation(summary = "Update company by ID", description = "Updates a company's information by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> updateCompany(
            @PathVariable Long id,
            @RequestBody @Valid CompanyUpdateDTO dto) {
        CompanyResponseDTO updated = companyService.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Delete company by ID", description = "Deletes a specific company by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}