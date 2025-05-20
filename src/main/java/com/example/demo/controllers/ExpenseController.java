package com.example.demo.controllers;

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
                            schema = @Schema(implementation = ExpenseEntity.class)))
    })
    @GetMapping
    public ResponseEntity<List<ExpenseEntity>> getAllExpenses() {
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
                            schema = @Schema(implementation = ExpenseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseEntity> getExpenseById(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        return ResponseEntity.ok(expense);
    }

    @Operation(
            summary = "Create a new expense",
            description = "Creates a new expense for a specific user, including category, amount, description, and date.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data of the new expense to be registered",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<ExpenseEntity> createExpense(@RequestBody ExpenseEntity expense) {
        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        expenseService.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
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
                            schema = @Schema(implementation = ExpenseEntity.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseEntity> updateExpense(@PathVariable Long id,
                                                       @RequestBody ExpenseEntity updatedExpense) {
        ExpenseEntity existing = expenseService.findById(id);

        existing.setCategory(updatedExpense.getCategory());
        existing.setDescription(updatedExpense.getDescription());
        existing.setAmount(updatedExpense.getAmount());
        existing.setDate(updatedExpense.getDate());

        expenseService.save(existing);
        return ResponseEntity.ok(existing);
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
        ExpenseEntity expense = expenseService.findById(id);
        expenseService.delete(expense);
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
        Double average = expenseService.getAverageExpense();
        return ResponseEntity.ok(average);
    }

    @Operation(
            summary = "Get average expense by user ID",
            description = "Calculates and returns the average of expenses for the user with the provided ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the user whose expenses will be averaged", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's average calculated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal error while calculating average")
    })
    @GetMapping("/averageByUserId/{id}")
    public ResponseEntity<Double> getAverageExpensesById(@PathVariable Long id) {
        Double average = expenseService.getAverageExpenseById(id);
        return ResponseEntity.ok(average);
    }
}
