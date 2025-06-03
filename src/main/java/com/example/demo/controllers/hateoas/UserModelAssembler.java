package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.UserController;
import org.apache.catalina.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler <UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO user) {
        return EntityModel.of(user,

                linkTo(methodOn(UserController.class).getUserById(user.getId(), null)).withSelfRel(),

                linkTo((methodOn(UserController.class).getAllUsers())).withRel("all-users")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserResponseDTO>> toCollectionModel(Iterable<? extends UserResponseDTO> users) {
        List<EntityModel<UserResponseDTO>> userModels = ((List<UserResponseDTO>)users).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel()
        );
    }

}
