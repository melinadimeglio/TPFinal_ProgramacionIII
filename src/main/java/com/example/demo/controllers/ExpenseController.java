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

@Tag(name = "Gastos", description = "Operaciones relacionadas con los gastos de los usuarios")

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(
            summary = "Obtener todos los gastos",
            description = "Devuelve una lista con todos los gastos registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de gastos obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class)))
    })
    // Obtener todos los gastos
    @GetMapping
    public ResponseEntity<List<ExpenseEntity>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
    }


    @Operation(
            summary = "Obtener un gasto por ID",
            description = "Devuelve un gasto específico según su ID si existe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Gasto no encontrado")
    })
    // Obtener un gasto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseEntity> getExpenseById(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        return ResponseEntity.ok(expense);
    }

    @Operation(
            summary = "Crear un nuevo gasto",
            description = "Crea un gasto nuevo para un usuario específico, incluyendo categoría, monto, descripción y fecha.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo gasto a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gasto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
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

    @Operation(
            summary = "Actualizar un gasto existente",
            description = "Actualiza la información de un gasto ya registrado usando su ID y los nuevos datos proporcionados.",
            parameters = {
                    @Parameter(name = "id", description = "ID del gasto a actualizar", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del gasto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Gasto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
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

    @Operation(
            summary = "Eliminar un gasto por ID",
            description = "Elimina el gasto correspondiente al ID proporcionado si existe.",
            parameters = {
                    @Parameter(name = "id", description = "ID del gasto a eliminar", required = true)
            }
    )
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


    @Operation(
            summary = "Obtener el promedio de gastos entre todos los usuarios",
            description = "Calcula y devuelve el promedio de todos los gastos registrados por todos los usuarios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al calcular el promedio")
    })
    // Promedio de gastos totales
    @GetMapping("/averageAllUsers")
    public ResponseEntity<Double> getAverageExpense() {
        Double average = expenseService.getAverageExpense();
        return ResponseEntity.ok(average);
    }

    @Operation(
            summary = "Obtener el promedio de gastos por ID de usuario",
            description = "Calcula y devuelve el promedio de gastos del usuario cuyo ID se proporciona."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio del usuario calculado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))),
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
