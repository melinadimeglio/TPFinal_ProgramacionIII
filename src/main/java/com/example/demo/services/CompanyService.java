package com.example.demo.services;

import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.entities.*;
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

    /*
    public CompanyResponseDTO save(CompanyCreateDTO companyCreateDTO) {
        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(companyCreateDTO.getEmail());
        credential.setPassword(passwordEncoder.encode(companyCreateDTO.getPassword()));

        //credential.se(user);
        //user.setCredential(credential);
        return companyMapper.save(user);
    }*/


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

    public void save(CompanyEntity company){
        if (companyRepository.existsByTaxId(company.getTaxId())){
            throw new IllegalArgumentException("El Tax ID ya se encuentra registrado en el sistema.");
        }
        companyRepository.save(company);
    }
}
