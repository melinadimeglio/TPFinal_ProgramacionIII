package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.CompanyController;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;@Component
public class CompanyModelAssembler implements RepresentationModelAssembler<CompanyResponseDTO, EntityModel<CompanyResponseDTO>> {

    @Override
    public EntityModel<CompanyResponseDTO> toModel(CompanyResponseDTO company) {
        EntityModel<CompanyResponseDTO> model = EntityModel.of(company);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_EMPRESA")) {
            model.add(linkTo(methodOn(CompanyController.class).getCompanyById(company.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_EMPRESAS")) {
            model.add(linkTo(methodOn(CompanyController.class).getAllCompanies(PageRequest.of(0, 10), null)).withRel("all-companies"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<CompanyResponseDTO>> toCollectionModel(Iterable<? extends CompanyResponseDTO> empresas) {
        List<EntityModel<CompanyResponseDTO>> empresaModels = ((List<CompanyResponseDTO>) empresas).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<CompanyResponseDTO>> collection = CollectionModel.of(empresaModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_EMPRESAS")) {
            collection.add(linkTo(methodOn(CompanyController.class).getAllCompanies(PageRequest.of(0, 10), null)).withRel("all-companies"));
        }

        return collection;
    }

    private Set<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) return Set.of();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
