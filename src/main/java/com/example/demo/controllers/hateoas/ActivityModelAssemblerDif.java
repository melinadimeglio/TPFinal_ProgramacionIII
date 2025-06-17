package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityCreateResponseDTO;
import com.example.demo.controllers.ActivityController;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ActivityModelAssemblerDif implements RepresentationModelAssembler<ActivityCreateResponseDTO, EntityModel<ActivityCreateResponseDTO>> {

    @Override
    public EntityModel<ActivityCreateResponseDTO> toModel (ActivityCreateResponseDTO activity) {
        EntityModel<ActivityCreateResponseDTO> model = EntityModel.of(activity);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ACTIVIDAD")) {
            model.add(linkTo(methodOn(ActivityController.class).getActivityById(activity.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_TODAS_ACTIVIDADES")) {
            model.add(linkTo(methodOn(ActivityController.class).getAllActivities(PageRequest.of(0, 10))).withRel("all-activities"));
        }

        return model;
    }

    private Set<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) return Set.of();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

}
