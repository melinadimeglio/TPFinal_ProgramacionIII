package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.TripController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TripModelAssembler implements RepresentationModelAssembler <TripResponseDTO, EntityModel<TripResponseDTO>> {

    @Override
    public EntityModel<TripResponseDTO> toModel(TripResponseDTO trip) {
        return EntityModel.of(trip,

                linkTo(methodOn(TripController.class).getTripById(trip.getId())).withSelfRel(),

                linkTo((methodOn(TripController.class).getAllTrips())).withRel("all-trips")
        );
    }

    @Override
    public CollectionModel<EntityModel<TripResponseDTO>> toCollectionModel(Iterable<? extends TripResponseDTO> trips) {
        List<EntityModel<TripResponseDTO>> tripModels = ((List<TripResponseDTO>)trips).stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(tripModels,
                linkTo(methodOn(TripController.class).getAllTrips()).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<TripResponseDTO>> toCollectionModelByUser(List<TripResponseDTO> trips, Long userId){
        List<EntityModel<TripResponseDTO>> tripModels = trips.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(tripModels,
                linkTo(methodOn(TripController.class).getTripsByUserId(userId)).withSelfRel(),
                linkTo(methodOn(TripController.class).getAllTrips()).withRel("all-trips")
        );
    }

}
