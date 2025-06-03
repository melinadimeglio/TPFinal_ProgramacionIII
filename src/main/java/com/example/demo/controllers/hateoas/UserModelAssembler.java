package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.UserController;
import org.apache.catalina.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler <UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserByIdPublic(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers(PageRequest.of(0, 10))).withRel("all-users")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserResponseDTO>> toCollectionModel(Iterable<? extends UserResponseDTO> users) {
        var userModels = StreamSupport.stream(users.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).getAllUsers(PageRequest.of(0, 10))).withSelfRel()
        );
    }
}
