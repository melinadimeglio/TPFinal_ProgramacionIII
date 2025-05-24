package com.example.demo.controllers;

import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.services.CompanyService;
import com.example.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Obtener todos las empresas
    @GetMapping
    public ResponseEntity<List<CompanyEntity>> getAllUsers() {
        return ResponseEntity.ok(companyService.findAll());
    }

    // Obtener un empresa por ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyEntity> getCompanyById(@PathVariable Long id) {
        CompanyEntity company = companyService.findById(id);
        return ResponseEntity.ok(company);
    }

    // Crear una nueva empresa
    @PostMapping
    public ResponseEntity<CompanyEntity> createCompany(@RequestBody @Valid CompanyEntity company) {
        if (company.getUsername() == null || company.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        companyService.save(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    // Actualizar una empresa existente
    @PutMapping("/{id}")
    public ResponseEntity<CompanyEntity> updateCompany(@PathVariable Long id,
                                                 @RequestBody @Valid CompanyEntity updatedCompany) {
        CompanyEntity existing = companyService.findById(id);

        existing.setUsername(updatedCompany.getUsername());
        existing.setEmail(updatedCompany.getEmail());
        existing.setPassword(updatedCompany.getPassword());
        existing.setTaxId(updatedCompany.getTaxId());
        existing.setPhone(updatedCompany.getPhone());
        existing.setLocation(updatedCompany.getLocation());
        existing.setActive(updatedCompany.isActive());
        existing.setActivities(updatedCompany.getActivities());

        companyService.save(existing);
        return ResponseEntity.ok(existing);
    }

    // Eliminar una empresa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        CompanyEntity company = companyService.findById(id);
        companyService.delete(company);
        return ResponseEntity.noContent().build();
    }

}
