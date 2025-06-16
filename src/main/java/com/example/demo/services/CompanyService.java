package com.example.demo.services;

import com.example.demo.DTOs.Company.Request.CompanyCreateDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.DTOs.Company.CompanyUpdateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.entities.*;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import com.example.demo.security.repositories.CredentialRepository;
import com.example.demo.security.repositories.RoleRepository;
import com.example.demo.security.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final RoleRepository roleRepository;
    private final JWTService jwtService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository,
                          CompanyMapper companyMapper,
                          CredentialRepository credentialRepository,
                          PasswordEncoder passwordEncoder, RoleRepository roleRepository, JWTService jwtService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    public CompanyResponseDTO save(CompanyCreateDTO dto) {
        if (companyRepository.existsByTaxId(dto.getTaxId())){
            throw new IllegalArgumentException("The Tax ID is already registered in the system.");
        }

        CompanyEntity companyEntity = companyMapper.toEntity(dto);

        RoleEntity userRole = roleRepository.findByRole(Role.ROLE_COMPANY)
                .orElseThrow(() -> new RuntimeException("Role COMPANY not found."));

        CompanyEntity savedCompany = companyRepository.save(companyEntity);
        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(dto.getEmail());
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setCompany(savedCompany);
        credential.setRoles(Set.of(userRole));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                credential.getEmail(),
                credential.getPassword(),
                credential.getAuthorities()
        );

        String refreshToken = jwtService.generateRefreshToken(userDetails);
        credential.setRefreshToken(refreshToken);

        credentialRepository.save(credential);

        return companyMapper.toDTO(savedCompany);
    }

    public CompanyResponseDTO findById(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company not found."));
        return companyMapper.toDTO(entity);
    }

    public Page<CompanyResponseDTO> findAll(Pageable pageable) {
        return companyRepository.findAllByActiveTrue(pageable)
                .map(companyMapper::toDTO);
    }

    public Page<CompanyResponseDTO> findAllInactive(Pageable pageable) {
        return companyRepository.findAllByActiveFalse(pageable)
                .map(companyMapper::toDTO);
    }

    public CompanyResponseDTO update(Long id, CompanyUpdateDTO dto) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        companyMapper.updateCompanyEntityFromDTO(dto, entity);

        CompanyEntity updated = companyRepository.save(entity);
        return companyMapper.toDTO(updated);
    }

    public CompanyResponseDTO getProfile(String username){
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        CompanyEntity company = credential.getCompany();
        return companyMapper.toDTO(company);
    }

    public void delete(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));
        CredentialEntity credential = entity.getCredential();
        entity.setActive(false);
        credential.setActive(false);
        entity.getActivities().forEach(activityEntity -> activityEntity.setAvailable(false));
        companyRepository.save(entity);
    }

    public void deleteOwn(String username) {
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        CompanyEntity company = credential.getCompany();
        company.setActive(false);
        company.getCredential().setActive(false);
        company.getActivities().forEach(activityEntity -> activityEntity.setAvailable(false));
        companyRepository.save(company);
        credentialRepository.save(credential);
    }

    public void restore(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));
        entity.setActive(true);
        entity.getCredential().setActive(true);
        entity.getActivities().forEach(activityEntity -> activityEntity.setAvailable(true));
        companyRepository.save(entity);
    }
}
