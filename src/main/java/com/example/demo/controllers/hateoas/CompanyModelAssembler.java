package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.CheckList.CheckListItemResponseDTO;
import com.example.demo.DTOs.Company.CompanyResponseDTO;
import com.example.demo.controllers.CheckListItemController;
import com.example.demo.controllers.CompanyController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompanyModelAssembler implements RepresentationModelAssembler<CompanyResponseDTO, EntityModel<CompanyResponseDTO>> {

    @Override
    public EntityModel<CompanyResponseDTO> toModel(CompanyResponseDTO company) {
        return EntityModel.of(company,

                linkTo(methodOn(CompanyController.class).getCompanyById(company.getId())).withSelfRel(),

                linkTo((methodOn(CompanyController.class).getAllCompanies())).withRel("all-companies")
        );    }

    @Override
    public CollectionModel<EntityModel<CompanyResponseDTO>> toCollectionModel(Iterable<? extends CompanyResponseDTO> companies) {
        List<EntityModel<CompanyResponseDTO>> companyModels = ((List<CompanyResponseDTO>)companies).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(companyModels,
                linkTo(methodOn(CompanyController.class).getAllCompanies()).withSelfRel()
        );
    }

}
