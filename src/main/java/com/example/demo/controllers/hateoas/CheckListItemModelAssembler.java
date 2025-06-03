package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.controllers.CheckListItemController;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CheckListItemModelAssembler implements RepresentationModelAssembler<CheckListItemResponseDTO, EntityModel<CheckListItemResponseDTO>> {

    @Override
    public EntityModel<CheckListItemResponseDTO> toModel(CheckListItemResponseDTO checklistItem) {
        return EntityModel.of(checklistItem,

                linkTo(methodOn(CheckListItemController.class).getById(checklistItem.getId())).withSelfRel(),

                linkTo((methodOn(CheckListItemController.class).getAll(PageRequest.of(0,10), null))).withRel("all-checklist-items")
        );
    }

    @Override
    public CollectionModel<EntityModel<CheckListItemResponseDTO>> toCollectionModel(Iterable<? extends CheckListItemResponseDTO> checklistItems) {
        List<EntityModel<CheckListItemResponseDTO>> checklistItemModels = ((List<CheckListItemResponseDTO>)checklistItems).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(checklistItemModels,
                linkTo(methodOn(CheckListItemController.class).getAll(PageRequest.of(0, 10), null)).withSelfRel()
        );
    }

}
