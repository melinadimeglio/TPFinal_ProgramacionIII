package com.example.demo.controllers;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.services.ExpenseService;
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

    // Obtener todos los gastos
    @GetMapping
    public ResponseEntity<List<ExpenseEntity>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    // Obtener un gasto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseEntity> getExpenseById(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        return ResponseEntity.ok(expense);
    }

    // Crear un nuevo gasto
    @PostMapping
    public ResponseEntity<ExpenseEntity> createExpense(@RequestBody ExpenseEntity expense) {
        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }
        expenseService.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

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

    // Eliminar un gasto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        ExpenseEntity expense = expenseService.findById(id);
        expenseService.delete(expense);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageExpense() {
        Double average = expenseService.getAverageExpense();
        return ResponseEntity.ok(average);
    }

}
