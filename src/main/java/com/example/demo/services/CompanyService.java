package com.example.demo.services;

import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyEntity> findAll(){
        return companyRepository.findAll();
    }

    public CompanyEntity findById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento."));
    }

    public void save(CompanyEntity company){
        if (companyRepository.existsByTaxId(company.getTax_id())){
            throw new IllegalArgumentException("El Tax ID ya se encuentra registrado en el sistema.");
        }
        companyRepository.save(company);
    }

    public void delete(CompanyEntity company){
        companyRepository.delete(company);
    }

}
