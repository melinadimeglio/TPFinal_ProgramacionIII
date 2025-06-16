package com.example.demo.config;

import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import com.example.demo.security.repositories.CredentialRepository;
import com.example.demo.security.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

        private final UserRepository userRepository;
        private final CompanyRepository companyRepository;
        private final CredentialRepository credentialRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        @PostConstruct
        @Transactional
        public void init() {
            initRoles();
            initAdmin();
            initUser();
            initCompany();
        }

        private void initRoles() {
            for (Role role : Role.values()) {
                roleRepository.findByRole(role).orElseGet(() -> {
                    RoleEntity newRole = RoleEntity.builder()
                            .role(role)
                            .permits(new HashSet<>()) //
                            .build();
                    return roleRepository.save(newRole);
                });
            }
        }

        private void initAdmin() {
            if (credentialRepository.findByEmail("administrador@gmail.com").isEmpty()) {
                UserEntity adminUser = UserEntity.builder()
                        .username("admin")
                        .dni("00000001")
                        .active(true)
                        .build();

                userRepository.save(adminUser);

                RoleEntity adminRole = roleRepository.findByRole(Role.ROLE_ADMIN).orElseThrow();

                CredentialEntity credential = CredentialEntity.builder()
                        .email("administrador@gmail.com")
                        .password(passwordEncoder.encode("Contra123_"))
                        .roles(Set.of(adminRole))
                        .user(adminUser)
                        .active(true)
                        .refreshToken("initialRefreshToken-admin")
                        .build();

                credentialRepository.save(credential);
            }
        }

        private void initUser() {
            if (credentialRepository.findByEmail("user@gmail.com").isEmpty()) {
                UserEntity normalUser = UserEntity.builder()
                        .username("usuario")
                        .dni("00000002")
                        .active(true)
                        .build();

                userRepository.save(normalUser);

                RoleEntity userRole = roleRepository.findByRole(Role.ROLE_USER).orElseThrow();

                CredentialEntity credential = CredentialEntity.builder()
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("Contra123_"))
                        .roles(Set.of(userRole))
                        .user(normalUser)
                        .active(true)
                        .refreshToken("initialRefreshToken-user")
                        .build();

                credentialRepository.save(credential);
            }
        }

        private void initCompany() {
            if (credentialRepository.findByEmail("company@gmail.com").isEmpty()) {
                CompanyEntity company = CompanyEntity.builder()
                        .username("company")
                        .taxId("30-00000001-9")
                        .description("Empresa demo")
                        .phone("+542231111111")
                        .location("Buenos Aires")
                        .active(true)
                        .build();

                companyRepository.save(company);

                RoleEntity companyRole = roleRepository.findByRole(Role.ROLE_COMPANY).orElseThrow();

                CredentialEntity credential = CredentialEntity.builder()
                        .email("company@gmail.com")
                        .password(passwordEncoder.encode("Contra123_"))
                        .roles(Set.of(companyRole))
                        .company(company)
                        .active(true)
                        .refreshToken("initialRefreshToken-company")
                        .build();

                credentialRepository.save(credential);
            }
        }
    }

