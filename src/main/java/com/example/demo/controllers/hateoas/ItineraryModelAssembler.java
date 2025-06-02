package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.ItineraryController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ItineraryModelAssembler implements RepresentationModelAssembler <ItineraryResponseDTO, EntityModel<ItineraryResponseDTO>> {

    @Override
    public EntityModel<ItineraryResponseDTO> toModel(ItineraryResponseDTO itinerary) {
        return EntityModel.of(itinerary,

                linkTo(methodOn(ItineraryController.class).getItineraryById(itinerary.getId())).withSelfRel(),

                linkTo((methodOn(ItineraryController.class).getAllItineraries())).withRel("all-itineraries")
        );
    }

    @Override
    public CollectionModel<EntityModel<ItineraryResponseDTO>> toCollectionModel(Iterable<? extends ItineraryResponseDTO> itineraries) {
        List<EntityModel<ItineraryResponseDTO>> itineraryModels = ((List<ItineraryResponseDTO>)itineraries).stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(itineraryModels,
                linkTo(methodOn(ItineraryController.class).getAllItineraries()).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<ItineraryResponseDTO>> toCollectionModelByUser(List<ItineraryResponseDTO> itineraries, Long userId){
        List<EntityModel<ItineraryResponseDTO>> itineraryModels = itineraries.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(itineraryModels,
                linkTo(methodOn(ItineraryController.class).getItinerariesByUserId(userId)).withSelfRel(),
                linkTo(methodOn(ItineraryController.class).getAllItineraries()).withRel("all-itineraries")
        );
    }

}
