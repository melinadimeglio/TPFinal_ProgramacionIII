package com.example.demo.controllers;

import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.controllers.hateoas.CompanyModelAssembler;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.services.CompanyService;
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

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CompanyResponseDTO>>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(companies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> getCompanyById(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.findById(id);
        return ResponseEntity.ok(assembler.toModel(company));
    }

    // Crear una nueva empresa (registro)
    @PostMapping
    public ResponseEntity<EntityModel<CompanyResponseDTO>> createCompany(@RequestBody @Valid CompanyCreateDTO dto) {
        CompanyResponseDTO company = companyService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(company));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CompanyResponseDTO>> updateCompany(@PathVariable Long id,
                                                                         @RequestBody @Valid CompanyUpdateDTO dto) {
        CompanyResponseDTO updated = companyService.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    // Eliminar una empresa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
