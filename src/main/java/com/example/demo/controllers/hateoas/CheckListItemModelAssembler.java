package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.CheckListItemController;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CheckListItemModelAssembler implements RepresentationModelAssembler<CheckListItemResponseDTO, EntityModel<CheckListItemResponseDTO>> {

    @Override
    public EntityModel<CheckListItemResponseDTO> toModel(CheckListItemResponseDTO checklistItem) {
        EntityModel<CheckListItemResponseDTO> model = EntityModel.of(checklistItem);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_CHECKLIST_ITEM")) {
            model.add(linkTo(methodOn(CheckListItemController.class).getById(checklistItem.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_CHECKLISTITEM")) {
            model.add(linkTo((methodOn(CheckListItemController.class).getAll(PageRequest.of(0,10), null))).withRel("all-checklist-items"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<CheckListItemResponseDTO>> toCollectionModel(Iterable<? extends CheckListItemResponseDTO> checklistItems) {
        List<EntityModel<CheckListItemResponseDTO>> itemsModels = ((List<CheckListItemResponseDTO>) checklistItems).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<CheckListItemResponseDTO>> collection = CollectionModel.of(itemsModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_TODOS_CHECKLISTITEM")) {
            collection.add(linkTo(methodOn(CheckListItemController.class).getAll(PageRequest.of(0, 10), null)).withSelfRel());
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
