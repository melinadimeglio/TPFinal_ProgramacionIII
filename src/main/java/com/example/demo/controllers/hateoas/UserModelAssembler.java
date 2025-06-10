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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler <UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO user) {
        EntityModel<UserResponseDTO> model = EntityModel.of(user);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_PERFIL")) {
            model.add(linkTo(methodOn(UserController.class).getUserByIdPublic(user.getId())).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_USUARIOS")) {
            model.add(linkTo(methodOn(UserController.class).getAllUsers(PageRequest.of(0, 10))).withRel("all-users"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<UserResponseDTO>> toCollectionModel(Iterable<? extends UserResponseDTO> users) {
        List<EntityModel<UserResponseDTO>> userModels = ((List<UserResponseDTO>) users).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<UserResponseDTO>> collection = CollectionModel.of(userModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_TODOS_USUARIOS")) {
            collection.add(linkTo(methodOn(UserController.class).getAllUsers(PageRequest.of(0, 10))).withSelfRel());
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
