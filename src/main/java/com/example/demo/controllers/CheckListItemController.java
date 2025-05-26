package com.example.demo.controllers;

import com.example.demo.DTOs.CheckList.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.services.CheckListItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Checklist Items", description = "Operaciones relacionadas con los ítems de las checklists")
@RestController
@RequestMapping("/checklist-items")
@RequiredArgsConstructor
public class CheckListItemController {

    private final CheckListItemService service;

    @Operation(summary = "Obtener todos los ítems", description = "Devuelve una lista con todos los ítems de todas las checklists.")
    @ApiResponse(responseCode = "200", description = "Ítems encontrados", content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<CheckListItemResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener un ítem por ID", description = "Devuelve el ítem de checklist correspondiente al ID proporcionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado", content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CheckListItemResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Crear un nuevo ítem",
            description = "Crea un ítem asociado a una checklist.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Datos del ítem a crear",
                    content = @Content(schema = @Schema(implementation = CheckListItemCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ítem creado exitosamente",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CheckListItemResponseDTO> createItem(
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListItemCreateDTO dto) {

        CheckListItemResponseDTO createdItem = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckListItemResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CheckListItemUpdateDTO dto) {
        CheckListItemResponseDTO updatedItem = service.update(id, dto);
        return ResponseEntity.ok(updatedItem);
    }




    @Operation(summary = "Eliminar un ítem", description = "Elimina un ítem de checklist por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ítem eliminado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}