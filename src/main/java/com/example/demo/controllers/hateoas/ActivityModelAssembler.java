package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityCreateResponseDTO;
import com.example.demo.DTOs.Filter.ActivityFilterDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.controllers.ActivityController;
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
public class ActivityModelAssembler implements RepresentationModelAssembler<ActivityResponseDTO, EntityModel<ActivityResponseDTO>> {

    @Override
    public EntityModel<ActivityResponseDTO> toModel(ActivityResponseDTO activity) {
        EntityModel<ActivityResponseDTO> model = EntityModel.of(activity);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ACTIVIDAD")) {
            model.add(linkTo(methodOn(ActivityController.class).getActivityById(activity.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_TODAS_ACTIVIDADES")) {
            model.add(linkTo(methodOn(ActivityController.class).getAllActivities(PageRequest.of(0, 10))).withRel("all-activities"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModel(Iterable<? extends ActivityResponseDTO> activities) {
        List<EntityModel<ActivityResponseDTO>> activityModels = ((List<ActivityResponseDTO>) activities).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ActivityResponseDTO>> collection = CollectionModel.of(activityModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_TODAS_ACTIVIDADES")) {
            collection.add(linkTo(methodOn(ActivityController.class).getAllActivities(PageRequest.of(0, 10))).withSelfRel());
        }

        return collection;
    }

    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModelByUser(List<ActivityResponseDTO> activities, Long userId) {
        List<EntityModel<ActivityResponseDTO>> activityModels = activities.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ActivityResponseDTO>> collection = CollectionModel.of(activityModels);
        Set<String> permisos = getAuthorities();

        ActivityFilterDTO filters = new ActivityFilterDTO();

        if (permisos.contains("VER_ACTIVIDAD_USUARIO")) {
            collection.add(
                    linkTo(methodOn(ActivityController.class)
                            .getActivitiesByUserId(userId, null, null, PageRequest.of(0, 10)))
                            .withSelfRel()
            );
        }

        if (permisos.contains("VER_TODAS_ACTIVIDADES")) {
            collection.add(linkTo(methodOn(ActivityController.class).getAllActivities(PageRequest.of(0, 10))).withRel("all-activities"));
        }

        return collection;
    }

    public CollectionModel<EntityModel<ActivityResponseDTO>> toCollectionModelByCompany(List<ActivityResponseDTO> activities, Long companyId) {
        List<EntityModel<ActivityResponseDTO>> activityModels = activities.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ActivityResponseDTO>> collection = CollectionModel.of(activityModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ACTIVIDAD_EMPRESA")) {
            collection.add(linkTo(methodOn(ActivityController.class).getByCompanyId(companyId, null, PageRequest.of(0, 10))).withSelfRel());
        }

        if (permisos.contains("VER_TODAS_ACTIVIDADES")) {
            collection.add(linkTo(methodOn(ActivityController.class).getAllActivities(PageRequest.of(0, 10))).withRel("all-activities"));
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