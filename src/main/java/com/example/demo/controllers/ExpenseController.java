package com.example.demo.controllers;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.services.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Obtener todos los gastos")
    @ApiResponse(responseCode = "200", description = "Lista de gastos obtenida correctamente")
    // Obtener todos los gastos
    @GetMapping
    public ResponseEntity<List<ExpenseEntity>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    @Operation(summary = "Obtener un gasto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto encontrado",
                    content = @Content(schema = @Schema(implementation = ExpenseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    })
    // Obtener un gasto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseEntity> getExpenseById(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        return ResponseEntity.ok(expense);
    }

    @Operation(summary = "Crear un nuevo gasto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gasto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    // Crear un nuevo gasto
    @PostMapping
    public ResponseEntity<ExpenseEntity> createExpense(@RequestBody ExpenseEntity expense) {
        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }
        expenseService.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @Operation(summary = "Actualizar un gasto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    })
    // Actualizar un gasto
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

    @Operation(summary = "Eliminar un gasto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gasto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    })
    // Eliminar un gasto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        expenseService.delete(expense);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener el promedio de gastos entre todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al calcular el promedio")
    })
    // Promedio de gastos totales
    @GetMapping("/averageAllUsers")
    public ResponseEntity<Double> getAverageExpense() {
        Double average = expenseService.getAverageExpense();
        return ResponseEntity.ok(average);
    }

    @Operation(summary = "Obtener el promedio de gastos por ID de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio del usuario calculado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al calcular el promedio")
    })
    // Promedio de gastos por usuario
    @GetMapping("/averageByUserId/{id}")
    public ResponseEntity<Double> getAverageExpensesById(@PathVariable Long id) {
        Double average = expenseService.getAverageExpenseById(id);
        return ResponseEntity.ok(average);
    }
}
