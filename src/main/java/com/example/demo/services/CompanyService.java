package com.example.demo.services;

import com.example.demo.DTOs.Company.CompanyCreateDTO;
import com.example.demo.DTOs.Company.CompanyResponseDTO;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public List<CompanyResponseDTO> findAll() {
        List<CompanyEntity> companies = companyRepository.findAll();
        return companyMapper.toResponseDTOList(companies);
    }

    public CompanyResponseDTO findById(Long id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la empresa con ID: " + id));
        return companyMapper.toResponseDTO(company);
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