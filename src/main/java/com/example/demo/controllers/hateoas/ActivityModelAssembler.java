package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.controllers.ActivityController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ActivityModelAssembler implements RepresentationModelAssembler<ActivityResponseDTO, EntityModel<ActivityResponseDTO>> {

    @Override
    public EntityModel<ActivityResponseDTO> toModel(ActivityResponseDTO activity) {
        return EntityModel.of(activity,

                linkTo(methodOn(ActivityController.class).getActivityById(activity.getId())).withSelfRel(),

                linkTo((methodOn(ActivityController.class).getAllActivities(null, null, null))).withRel("all-activities")
        );
    }

    @Override
    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModel(Iterable<? extends ActivityResponseDTO> activities) {
        List<EntityModel<ActivityResponseDTO>> activityModels = ((List<ActivityResponseDTO>)activities).stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(activityModels,
                linkTo(methodOn(ActivityController.class).getAllActivities(null, null, null)).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModelByUser(List<ActivityResponseDTO> activities, Long userId){
        List<EntityModel<ActivityResponseDTO>> activityModels = activities.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(activityModels,

        );
    }

    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModelByCompany(List<ActivityResponseDTO> activities, Long companyId){
        List<EntityModel<ActivityResponseDTO>> activityModels = activities.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(activityModels,

        );
    }
}