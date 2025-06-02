package com.example.demo.services;

import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.entities.*;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import com.example.demo.security.repositories.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CompanyService(CompanyRepository companyRepository,
                          CompanyMapper companyMapper,
                          CredentialRepository credentialRepository,
                          PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CompanyResponseDTO save(CompanyCreateDTO dto) {
        if (companyRepository.existsByTaxId(dto.getTaxId())){
            throw new IllegalArgumentException("El Tax ID ya se encuentra registrado en el sistema.");
        }

        CompanyEntity company = companyMapper.toEntity(dto);
        CompanyEntity savedCompany = companyRepository.save(company);

        RoleEntity companyRole = RoleEntity.builder()
                .role(Role.ROLE_COMPANY)
                .build();

        CredentialEntity credential = CredentialEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(Set.of(companyRole))
                .company(savedCompany)
                .active(true)
                .build();

        credentialRepository.save(credential);

        return companyMapper.toDTO(savedCompany);
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

    public CompanyResponseDTO update(Long id, CompanyUpdateDTO dto) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        companyMapper.updateCompanyEntityFromDTO(dto, entity);

        CompanyEntity updated = companyRepository.save(entity);
        return companyMapper.toDTO(updated);
    }

    public void delete(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));
        companyRepository.delete(entity);
    }
}
