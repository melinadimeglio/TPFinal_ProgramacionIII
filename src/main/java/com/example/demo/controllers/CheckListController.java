package com.example.demo.controllers;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.services.CheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checklists")
public class CheckListController {

    private final CheckListService checkListService;

    @Autowired
    public CheckListController(CheckListService checkListService) {
        this.checkListService = checkListService;
    }

    // Obtener todos los ítems del checklist
    @GetMapping
    public ResponseEntity<List<CheckListEntity>> getAllItems() {
        return ResponseEntity.ok(checkListService.findAll());
    }

    // Obtener un ítem por ID
    @GetMapping("/{id}")
    public ResponseEntity<CheckListEntity> getItemById(@PathVariable Long id) {
        CheckListEntity item = checkListService.findById(id);
        return ResponseEntity.ok(item);
    }

    // Crear un nuevo ítem
    @PostMapping
    public ResponseEntity<CheckListEntity> createItem(@RequestBody CheckListEntity item) {
        if (item.getItem() == null || item.getItem().isBlank()) {
            throw new IllegalArgumentException("El ítem no puede estar vacío.");
        }
        checkListService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    // Actualizar un ítem
    @PutMapping("/{id}")
    public ResponseEntity<CheckListEntity> updateItem(@PathVariable Long id,
                                                      @RequestBody CheckListEntity updatedItem) {
        CheckListEntity existing = checkListService.findById(id);

        existing.setItem(updatedItem.getItem());
        existing.setStatus(updatedItem.isStatus());

        checkListService.save(existing);
        return ResponseEntity.ok(existing);
    }

    // Eliminar un ítem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        CheckListEntity item = checkListService.findById(id);
        checkListService.delete(item);
        return ResponseEntity.noContent().build();
    }
}

