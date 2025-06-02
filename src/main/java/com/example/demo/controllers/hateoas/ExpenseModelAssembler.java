package com.example.demo.controllers.hateoas;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.controllers.ActivityController;
import com.example.demo.controllers.CheckListItemController;
import com.example.demo.controllers.ExpenseController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExpenseModelAssembler implements RepresentationModelAssembler<ExpenseResponseDTO, EntityModel<ExpenseResponseDTO>> {

    @Override
    public EntityModel<ExpenseResponseDTO> toModel(ExpenseResponseDTO expense) {
        return EntityModel.of(expense,

                linkTo(methodOn(ExpenseController.class).getExpenseById(expense.getId())).withSelfRel(),

                linkTo((methodOn(ExpenseController.class).getAllExpenses())).withRel("all-checklist-items")
        );
    }

    @Override
    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModel(Iterable<? extends ExpenseResponseDTO> expenses) {
        List<EntityModel<ExpenseResponseDTO>> expenseModels = ((List<ExpenseResponseDTO>)expenses).stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(expenseModels,
                linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel()
        );
    }

    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModelByUser (List<ExpenseResponseDTO> expenses, Long userId){
        List<EntityModel<ExpenseResponseDTO>> expenseModels = expenses.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(expenseModels,
                linkTo(methodOn(ExpenseController.class).findByUserId(userId)).withSelfRel(),
                linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("all-expenses")
        );
    }

    public CollectionModel<EntityModel<ExpenseResponseDTO>> toCollectionModelByTrip (List<ExpenseResponseDTO> expenses, Long tripId){
        List<EntityModel<ExpenseResponseDTO>> expenseModels = expenses.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(expenseModels,
                linkTo(methodOn(ExpenseController.class).getExpensesByTripId(tripId)).withSelfRel(),
                linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("all-expenses")
        );
    }
}
