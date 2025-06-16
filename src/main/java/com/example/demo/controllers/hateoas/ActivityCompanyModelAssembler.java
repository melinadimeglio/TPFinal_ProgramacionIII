package com.example.demo.controllers.hateoas;


import com.example.demo.DTOs.Activity.Response.ActivityCompanyResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ActivityCompanyModelAssembler implements RepresentationModelAssembler<ActivityCompanyResponseDTO, EntityModel<ActivityCompanyResponseDTO>> {

    @Override
    public EntityModel<ActivityCompanyResponseDTO> toModel(ActivityCompanyResponseDTO activity) {
        return EntityModel.of(activity);
    }
}