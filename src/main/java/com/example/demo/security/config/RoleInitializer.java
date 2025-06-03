package com.example.demo.security.config;

import com.example.demo.security.entities.PermitEntity;
import com.example.demo.security.enums.Permit;
import com.example.demo.security.mapping.RolePermitMapping;
import com.example.demo.security.repositories.PermitRepository;
import com.example.demo.security.repositories.RoleRepository;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class RoleInitializer {

    private final RoleRepository roleRepository;
    private final PermitRepository permitRepository;
    private final RolePermitMapping rolePermitMapping;

    @Autowired
    public RoleInitializer(RoleRepository roleRepository,
                           PermitRepository permitRepository,
                           RolePermitMapping rolePermitMapping) {
        this.roleRepository = roleRepository;
        this.permitRepository = permitRepository;
        this.rolePermitMapping = rolePermitMapping;
    }

    @PostConstruct
    public void initRoles() {

        for (Permit permitEnum : Permit.values()) {
            permitRepository.findByPermit(permitEnum)
                    .orElseGet(() -> {
                        PermitEntity newPermit = new PermitEntity();
                        newPermit.setPermit(permitEnum);
                        return permitRepository.save(newPermit);
                    });
        }

        for (Role roleEnum : Role.values()) {

            RoleEntity roleEntity = roleRepository.findByRole(roleEnum)
                    .orElseGet(() -> {
                        RoleEntity newRole = new RoleEntity();
                        newRole.setRole(roleEnum);
                        return roleRepository.save(newRole);
                    });

            Set<Permit> permits = rolePermitMapping.getPermitsForRole(roleEnum);

            Set<PermitEntity> permitEntities = permits.stream()
                    .map(permitEnum -> permitRepository.findByPermit(permitEnum).orElseThrow())
                    .collect(Collectors.toSet());

            roleEntity.getPermits().clear();
            roleEntity.getPermits().addAll(permitEntities);
            roleRepository.save(roleEntity);
        }
    }
}