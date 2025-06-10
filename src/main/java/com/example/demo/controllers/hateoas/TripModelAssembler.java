package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.TripController;
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
public class TripModelAssembler implements RepresentationModelAssembler <TripResponseDTO, EntityModel<TripResponseDTO>> {

    @Override
    public EntityModel<TripResponseDTO> toModel(TripResponseDTO trip) {
        EntityModel<TripResponseDTO> model = EntityModel.of(trip);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_VIAJE")) {
            model.add(linkTo(methodOn(TripController.class).getTripById(trip.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_VIAJES")) {
            model.add(linkTo((methodOn(TripController.class).getAllTrips())).withRel("all-trips"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<TripResponseDTO>> toCollectionModel(Iterable<? extends TripResponseDTO> trips) {
        List<EntityModel<TripResponseDTO>> tripModels = ((List<TripResponseDTO>) trips).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<TripResponseDTO>> collection = CollectionModel.of(tripModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_VIAJES")) {
            collection.add(linkTo(methodOn(TripController.class).getAllTrips()).withSelfRel());
        }

        return collection;
    }

    public CollectionModel<EntityModel<TripResponseDTO>> toCollectionModelByUser(List<TripResponseDTO> trips, Long userId) {
        List<EntityModel<TripResponseDTO>> tripModels = trips.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<TripResponseDTO>> collection = CollectionModel.of(tripModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_VIAJE_USUARIO")) {
            collection.add(linkTo(methodOn(TripController.class).getTripsByUserId(userId, null)).withSelfRel());
        }

        if (permisos.contains("VER_VIAJES")) {
            collection.add(linkTo(methodOn(TripController.class).getAllTrips()).withRel("all-trips"));
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
