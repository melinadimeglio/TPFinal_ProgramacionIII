package com.example.demo.services;


import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Company.CompanyResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }


    public CompanyResponseDTO findById(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        return companyMapper.toDTO(entity);
    }

    public List<CompanyResponseDTO> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CompanyResponseDTO createCompany(CompanyCreateDTO createDTO) {
        if (companyRepository.existsByTaxId(createDTO.getTaxId())) {
            throw new IllegalArgumentException("El Tax ID ya está registrado en el sistema.");
        }
        CompanyEntity company = companyMapper.toEntity(createDTO);
        CompanyEntity saved = companyRepository.save(company);
        return companyMapper.toResponseDTO(saved);
    }

    public void deleteById(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new NoSuchElementException("No se encontró la empresa con ID: " + id);
        }
        companyRepository.deleteById(id);
    }
}