package com.example.demo.controllers;

import com.example.demo.DTOs.Expense.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.services.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expenses", description = "Operations related to user expenses")
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(
            summary = "Get all expenses",
            description = "Returns a list of all expenses registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense list successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
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
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }



    @Operation(
            summary = "Get expenses by user ID",
            description = "Retrieves all expenses associated with a specific user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found or no expenses for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> findByUserId(@PathVariable Long userId) {
        List<ExpenseResponseDTO> expenses = expenseService.findByUserId(userId);
        return ResponseEntity.ok(expenses);
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
            @ApiResponse(responseCode = "201", description = "Expense successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<Void> createExpense(@RequestBody ExpenseCreateDTO dto) {
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        expenseService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long id,
                                                       @RequestBody ExpenseUpdateDTO dto) {
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get average expense across all users",
            description = "Calculates and returns the average of all expenses registered by all users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Average calculated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "500", description = "Internal error while calculating average")
    })
    @GetMapping("/averageAllUsers")
    public ResponseEntity<Double> getAverageExpense() {
        return ResponseEntity.ok(expenseService.getAverageExpense());
    }

    @Operation(summary = "Get average expense by user ID",
            description = "Returns average of all expenses for a specific user.",
            parameters = @Parameter(name = "id", description = "ID of the user", required = true))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User average calculated",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/averageByUserId/{id}")
    public ResponseEntity<Double> getAverageExpensesByUser(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getAverageExpenseByUserId(id));
    }
}
