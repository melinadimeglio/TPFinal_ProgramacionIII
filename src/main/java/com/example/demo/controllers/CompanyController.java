package com.example.demo.controllers;

import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.controllers.hateoas.CompanyModelAssembler;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@Tag(name = "Companies", description = "Operations related to company management")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyModelAssembler assembler;
    private final PagedResourcesAssembler<CompanyResponseDTO> pagedResourcesAssembler;

    @Autowired
    public CompanyController(CompanyService companyService, CompanyModelAssembler assembler, PagedResourcesAssembler<CompanyResponseDTO> pagedResourcesAssembler) {
        this.companyService = companyService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Get all companies", description = "Returns a list of all companies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('VER_EMPRESAS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CompanyResponseDTO>>> getAllCompanies(
            Pageable pageable,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getCompany() != null) {
            CompanyResponseDTO company = companyService.findById(credential.getCompany().getId());
            PagedModel<EntityModel<CompanyResponseDTO>> model = PagedModel.of(
                    List.of(assembler.toModel(company)),
                    new PagedModel.PageMetadata(1, 0, 1)
            );
            return ResponseEntity.ok(model);
        }

        Page<CompanyResponseDTO> companies = companyService.findAll(pageable);
        PagedModel<EntityModel<CompanyResponseDTO>> model = pagedResourcesAssembler.toModel(companies, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get all inactive companies",
            description = "Retrieves a paginated list of all inactive companies in the system. " +
                    "If the authenticated user is associated with a company, only their own company's data is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @PreAuthorize("hasAuthority('VER_EMPRESAS')")
    @GetMapping("/inactive")
    public ResponseEntity<PagedModel<EntityModel<CompanyResponseDTO>>> getAllCompaniesInactive(
            Pageable pageable,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getCompany() != null) {
            CompanyResponseDTO company = companyService.findById(credential.getCompany().getId());
            PagedModel<EntityModel<CompanyResponseDTO>> model = PagedModel.of(
                    List.of(assembler.toModel(company)),
                    new PagedModel.PageMetadata(1, 0, 1)
            );
            return ResponseEntity.ok(model);
        }

        Page<CompanyResponseDTO> companies = companyService.findAllInactive(pageable);
        PagedModel<EntityModel<CompanyResponseDTO>> model = pagedResourcesAssembler.toModel(companies, assembler);
        return ResponseEntity.ok(model);
    }


    @Operation(summary = "Get company by ID", description = "Returns a specific company by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PreAuthorize("hasAuthority('VER_EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> getCompanyById(@PathVariable Long id,
                                                                          @AuthenticationPrincipal CredentialEntity credential) {
        Long myCompanyId = credential.getCompany().getId();

        if (!myCompanyId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CompanyResponseDTO company = companyService.findById(id);
        return ResponseEntity.ok(assembler.toModel(company));
    }

    @GetMapping("/me")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        CompanyResponseDTO profile = companyService.getProfile(username);
        return ResponseEntity.ok(assembler.toModel(profile));
    }


    @Operation(summary = "Create a new company", description = "Creates a new company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAuthority('CREAR_COMPANY')")
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
    @PreAuthorize("hasAuthority('MODIFICAR_EMPRESA')")
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
    @PreAuthorize("hasAuthority('ELIMINAR_EMPRESA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ELIMINAR_EMPRESA')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnCompany(Authentication authentication) {
        String username = authentication.getName();
        companyService.deleteOwn(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restore a company",
            description = "Reactivates a company that was previously deleted (soft-deleted) by setting its status to active."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Company restored successfully. No content is returned in the response body."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_EMPRESA')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreCompany(@PathVariable Long id) {
        companyService.restore(id);
        return ResponseEntity.noContent().build();
    }

}