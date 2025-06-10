package com.example.demo.controllers.hateoas;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.CheckListController;
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
public class CheckListModelAssembler implements RepresentationModelAssembler<CheckListResponseDTO, EntityModel<CheckListResponseDTO>> {


    @Override
    public EntityModel<CheckListResponseDTO> toModel(CheckListResponseDTO checklist) {
        EntityModel<CheckListResponseDTO> model = EntityModel.of(checklist);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_CHECKLIST")) {
            model.add(linkTo(methodOn(CheckListController.class).getById(checklist.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_CHECKLIST")) {
            model.add(linkTo((methodOn(CheckListController.class).getAll(PageRequest.of(0, 10)))).withRel("all-checklists"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<CheckListResponseDTO>> toCollectionModel(Iterable<? extends CheckListResponseDTO> checklists) {
        List<EntityModel<CheckListResponseDTO>> checklistsModels = ((List<CheckListResponseDTO>) checklists).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<CheckListResponseDTO>> collection = CollectionModel.of(checklistsModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_TODOS_CHECKLIST")) {
            collection.add(linkTo(methodOn(CheckListController.class).getAll(PageRequest.of(0, 10))).withSelfRel());
        }

        return collection;
    }

    public CollectionModel<EntityModel<CheckListResponseDTO>> toCollectionModelByUser(List<CheckListResponseDTO> checklists, Long userId) {
        List<EntityModel<CheckListResponseDTO>> checklistModels = checklists.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<CheckListResponseDTO>> collection = CollectionModel.of(checklistModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_CHECKLIST_USER")) {
            collection.add(linkTo(methodOn(CheckListController.class).getByUser(userId, null, PageRequest.of(0,10))).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_CHECKLIST")) {
            collection.add(linkTo(methodOn(CheckListController.class).getAll(PageRequest.of(0, 10))).withRel("all-checklists")
            );
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
