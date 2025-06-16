package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Expense.Response.ExpenseResumeDTO;
import com.example.demo.controllers.ExpenseController;
import org.springframework.hateoas.CollectionModel;
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
    public class ExpenseResumeModelAssembler implements RepresentationModelAssembler<ExpenseResumeDTO, EntityModel<ExpenseResumeDTO>> {

        @Override
        public EntityModel<ExpenseResumeDTO> toModel(ExpenseResumeDTO expense) {
            EntityModel<ExpenseResumeDTO> model = EntityModel.of(expense);
            Set<String> permisos = getAuthorities();

            if (permisos.contains("VER_GASTO")) {
                model.add(linkTo(methodOn(ExpenseController.class).getExpenseById(expense.getId(), null)).withSelfRel());
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

