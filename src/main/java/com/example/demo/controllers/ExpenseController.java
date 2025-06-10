package com.example.demo.controllers;

import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.controllers.hateoas.ExpenseModelAssembler;
import com.example.demo.enums.ExpenseCategory;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expenses", description = "Operations related to user expenses")
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseModelAssembler assembler;
    private final PagedResourcesAssembler<ExpenseResponseDTO> pagedResourcesAssembler;

    @Autowired
    public ExpenseController(ExpenseService expenseService, ExpenseModelAssembler assembler, PagedResourcesAssembler<ExpenseResponseDTO> pagedResourcesAssembler) {
        this.expenseService = expenseService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Get all expenses",
            description = "Returns a list of all expenses registered in the system. You can optionally filter by category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense list successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('VER_TODOS_GASTOS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> getAllExpenses(
            Pageable pageable,
            @RequestParam(required = false) ExpenseCategory category) {

        Page<ExpenseResponseDTO> expenses;

        if (category != null) {
            expenses = expenseService.findByCategory(category, pageable);
        } else {
            expenses = expenseService.findAll(pageable);
        }
        PagedModel<EntityModel<ExpenseResponseDTO>> model = pagedResourcesAssembler.toModel(expenses, assembler);

        return ResponseEntity.ok(model);
    }


    @Operation(
            summary = "Get an expense by ID",
            description = "Returns a specific expense by its ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the expense to retrieve", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @PreAuthorize("hasAuthority('VER_GASTO')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ExpenseResponseDTO>> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDTO expense = expenseService.findById(id);

        return ResponseEntity.ok(assembler.toModel(expense));
    }

    @Operation(
            summary = "Get expenses by user ID",
            description = "Retrieves all expenses associated with a specific user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this resource"),
            @ApiResponse(responseCode = "404", description = "User not found or no expenses for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> findByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential,
            Pageable pageable) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<ExpenseResponseDTO> expenses = expenseService.findByUserId(userId, pageable);
        PagedModel<EntityModel<ExpenseResponseDTO>> model = pagedResourcesAssembler.toModel(expenses, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Create a new expense",
            description = "Creates a new expense for a specific user, including category, amount, description, and date.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the new expense to be registered",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseCreateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense successfully created",
                    content = @Content(schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_GASTO')")
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @RequestBody @Valid ExpenseCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        ExpenseResponseDTO createdExpense = expenseService.save(dto, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }


    @Operation(
            summary = "Update an existing expense",
            description = "Updates an existing expense using its ID and the provided new data.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the expense to update", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated data of the expense",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_GASTO')")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long id,
                                                       @RequestBody @Valid ExpenseUpdateDTO dto) {
        expenseService.update(id, dto);
        return ResponseEntity.ok(expenseService.findById(id));
    }


    @Operation(
            summary = "Delete an expense by ID",
            description = "Deletes the expense corresponding to the provided ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the expense to delete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_GASTO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restore an expense",
            description = "Reactivates an expense that was previously deleted (soft-deleted) by setting its status to active."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Expense restored successfully. No content is returned in the response body."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Expense not found"
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_GASTO')")
    @PutMapping("/expenses/restore/{id}")
    public ResponseEntity<Void> restoreExpense(@PathVariable Long id) {
        expenseService.restore(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get total-average of expenses (not divided) by user ID",
            description = "Returns the average of full expense amounts where the user participated, regardless of how many users shared the cost.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user to calculate the average", required = true)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User average calculated",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('VER_PROMEDIO_USUARIO')")
    @GetMapping("/averageByUserId/{userId}")
    public ResponseEntity<Double> getAverageExpensesByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(expenseService.getAverageExpenseByUserId(userId));
    }


    @Operation(
            summary = "Get expenses by trip ID",
            description = "Retrieves all expenses associated with a specific trip ID.",
            parameters = {
                    @Parameter(name = "tripId", description = "ID of the trip", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found or no expenses for trip"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('VER_GASTOS_VIAJE')")
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> getExpensesByTripId(@PathVariable Long tripId, Pageable pageable) {
        Page<ExpenseResponseDTO> expenses = expenseService.findByTripId(tripId, pageable);
        PagedModel<EntityModel<ExpenseResponseDTO>> model = pagedResourcesAssembler.toModel(expenses, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get real average expense by user ID",
            description = "Calcula el promedio real de lo que un usuario debe pagar considerando los gastos compartidos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
    @GetMapping("/realAverageByUserId/{userId}")
    public ResponseEntity<Double> getRealAverageExpense(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(expenseService.getRealAverageExpenseByUser(userId));
    }

    @Operation(
            summary = "Get average expense by trip ID",
            description = "Returns the average amount of all expenses associated with a specific trip."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average calculated successfully",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found or no expenses for trip")
    })
    @PreAuthorize("hasAuthority('VER_PROMEDIO_VIAJE')")
    @GetMapping("/averageByTripId/{tripId}")
    public ResponseEntity<Double> getAverageExpensesByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(expenseService.getAverageExpenseByTripId(tripId));
    }

    @Operation(
            summary = "Get total expenses by trip ID",
            description = "Returns the total amount of all expenses registered for a specific trip."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total calculated successfully",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found or no expenses for trip")
    })
    @PreAuthorize("hasAuthority('VER_TOTAL_GASTO_VIAJE')")
    @GetMapping("/totalByTripId/{tripId}")
    public ResponseEntity<Double> getTotalExpensesByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(expenseService.getTotalExpenseByTripId(tripId));
    }

    @Operation(
            summary = "Get total real expenses by user ID",
            description = "Returns the total amount a user must pay, considering the expense is divided among participants."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total calculated successfully",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
    @GetMapping("/realTotalByUserId/{userId}")
    public ResponseEntity<Double> getTotalRealExpensesByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(expenseService.getTotalRealExpenseByUser(userId));
    }

}
