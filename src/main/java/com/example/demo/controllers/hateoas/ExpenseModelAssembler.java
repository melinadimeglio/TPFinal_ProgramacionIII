package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.CheckListItemController;
import com.example.demo.controllers.ExpenseController;
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
public class ExpenseModelAssembler implements RepresentationModelAssembler<ExpenseResponseDTO, EntityModel<ExpenseResponseDTO>> {

    @Override
    public EntityModel<ExpenseResponseDTO> toModel(ExpenseResponseDTO expense) {
        EntityModel<ExpenseResponseDTO> model = EntityModel.of(expense);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_GASTO")) {
            model.add(linkTo(methodOn(ExpenseController.class).getExpenseById(expense.getId(), null)).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_GASTOS")) {
            model.add(linkTo((methodOn(ExpenseController.class).getAllExpenses(PageRequest.of(0,10), null))).withRel("all-expenses"));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModel(Iterable<? extends ExpenseResponseDTO> expenses) {
        List<EntityModel<ExpenseResponseDTO>> expenseModels = ((List<ExpenseResponseDTO>) expenses).stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ExpenseResponseDTO>> collection = CollectionModel.of(expenseModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_TODOS_GASTOS")) {
            collection.add(linkTo(methodOn(ExpenseController.class).getAllExpenses(PageRequest.of(0,10),null)).withSelfRel());
        }

        return collection;
    }

    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModelByUser(List<ExpenseResponseDTO> expenses, Long userId) {
        List<EntityModel<ExpenseResponseDTO>> expenseModels = expenses.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ExpenseResponseDTO>> collection = CollectionModel.of(expenseModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_GASTO_USUARIO")) {
            collection.add(linkTo(methodOn(ExpenseController.class)
                    .findByUserId(userId, null, null, PageRequest.of(0, 10)))
                    .withSelfRel());
        }

        if (permisos.contains("VER_TODOS_GASTOS")) {
            collection.add(linkTo(methodOn(ExpenseController.class).getAllExpenses(PageRequest.of(0,10),null)).withRel("all-expenses"));
        }

        return collection;
    }

    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModelByTrip(List<ExpenseResponseDTO> expenses, Long tripId) {
        List<EntityModel<ExpenseResponseDTO>> expenseModels = expenses.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<EntityModel<ExpenseResponseDTO>> collection = CollectionModel.of(expenseModels);
        Set<String> permisos = getAuthorities();

        if (permisos.contains("VER_GASTOS_VIAJES_TOTAL")) {
            collection.add(linkTo(methodOn(ExpenseController.class).getExpensesByTripId(tripId, PageRequest.of(0, 10))).withSelfRel());
        }

        if (permisos.contains("VER_TODOS_GASTOS")) {
            collection.add(linkTo(methodOn(ExpenseController.class).getAllExpenses(PageRequest.of(0,10),null)).withRel("all-expenses"));
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
