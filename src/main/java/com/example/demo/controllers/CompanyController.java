package com.example.demo.controllers;


import com.example.demo.DTOs.Company.CompanyResponseDTO;
import com.example.demo.controllers.hateoas.CompanyModelAssembler;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.services.CompanyService;
import com.example.demo.services.UserService;
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

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyCreateDTO createDTO) {
        CompanyResponseDTO responseDTO = companyService.createCompany(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
