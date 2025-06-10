package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.ItineraryController;
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
public class ItineraryModelAssembler implements RepresentationModelAssembler <ItineraryResponseDTO, EntityModel<ItineraryResponseDTO>> {

    @Override
    public EntityModel<ItineraryResponseDTO> toModel(ItineraryResponseDTO itinerary) {
        EntityModel<ItineraryResponseDTO> model = EntityModel.of(itinerary);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ITINERARIO")) {
            model.add(linkTo(methodOn(ItineraryController.class).getItineraryById(itinerary.getId())).withSelfRel());
        }

        if (permisos.contains("VER_ITINERARIOS")) {
            model.add(linkTo((methodOn(ItineraryController.class).getAllItineraries(PageRequest.of(0,10)))).withRel("all-itineraries"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<ItineraryResponseDTO>> toCollectionModel(Iterable<? extends ItineraryResponseDTO> itineraries) {
        List<EntityModel<ItineraryResponseDTO>> itineraryModels = ((List<ItineraryResponseDTO>) itineraries).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ItineraryResponseDTO>> collection = CollectionModel.of(itineraryModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ITINERARIOS")) {
            collection.add(linkTo(methodOn(ItineraryController.class).getAllItineraries(PageRequest.of(0,10))).withSelfRel());
        }

        return collection;
    }

    public CollectionModel<EntityModel<ItineraryResponseDTO>> toCollectionModelByUser(List<ItineraryResponseDTO> itineraries, Long userId) {
        List<EntityModel<ItineraryResponseDTO>> itineraryModels = itineraries.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ItineraryResponseDTO>> collection = CollectionModel.of(itineraryModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_ITINERARIO_USUARIO")) {
            collection.add(linkTo(methodOn(ItineraryController.class).getItinerariesByUserId(PageRequest.of(0,10), userId, null)).withSelfRel());
        }

        if (permisos.contains("VER_ITINERARIOS")) {
            collection.add(linkTo(methodOn(ItineraryController.class).getAllItineraries(PageRequest.of(0,10))).withRel("all-itineraries"));
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
