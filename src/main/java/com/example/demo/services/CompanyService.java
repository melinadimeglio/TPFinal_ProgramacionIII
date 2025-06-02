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

    public void save(CompanyEntity company){
        if (companyRepository.existsByTaxId(company.getTaxId())){
            throw new IllegalArgumentException("El Tax ID ya se encuentra registrado en el sistema.");
        }
        companyRepository.save(company);
    }

    public void delete(CompanyEntity company){
        companyRepository.delete(company);
    }

}
