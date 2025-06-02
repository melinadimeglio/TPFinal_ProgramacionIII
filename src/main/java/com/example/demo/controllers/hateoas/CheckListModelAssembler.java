package com.example.demo.controllers.hateoas;
import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
import com.example.demo.controllers.CheckListController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CheckListModelAssembler implements RepresentationModelAssembler<CheckListResponseDTO, EntityModel<CheckListResponseDTO>> {

    @Override
    public EntityModel<CheckListResponseDTO> toModel(CheckListResponseDTO checklist) {
        return EntityModel.of(checklist,

                linkTo(methodOn(CheckListController.class).getById(checklist.getId())).withSelfRel(),

                linkTo((methodOn(CheckListController.class).getAll())).withRel("all-checklists")
        );    }

    @Override
    public CollectionModel<EntityModel<CheckListResponseDTO>> toCollectionModel(Iterable<? extends CheckListResponseDTO> checklists) {
        List<EntityModel<CheckListResponseDTO>> checklistModels = ((List<CheckListResponseDTO>)checklists).stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(checklistModels,
                linkTo(methodOn(CheckListController.class).getAll()).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<CheckListResponseDTO>> toCollectionModelByUser(List<CheckListResponseDTO> checklists, Long userId){
        List<EntityModel<CheckListResponseDTO>> checklistModels = checklists.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(checklistModels,
                linkTo(methodOn(CheckListController.class).getByUser(userId)).withSelfRel(),
                linkTo(methodOn(CheckListController.class).getAll()).withRel("all-checklists")
        );
    }
}
