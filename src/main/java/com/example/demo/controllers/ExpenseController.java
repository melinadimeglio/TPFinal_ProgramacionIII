    package com.example.demo.controllers;

    import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
    import com.example.demo.DTOs.CheckList.Response.CheckListResponseDTO;
    import com.example.demo.DTOs.Company.Response.CompanyResponseDTO;
    import com.example.demo.DTOs.Filter.ExpenseFilterDTO;
    import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
    import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
    import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
    import com.example.demo.DTOs.Expense.Response.ExpenseResumeDTO;
    import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
    import com.example.demo.controllers.hateoas.ExpenseModelAssembler;
    import com.example.demo.controllers.hateoas.ExpenseResumeModelAssembler;
    import com.example.demo.enums.ExpenseCategory;
    import com.example.demo.exceptions.OwnershipException;
    import com.example.demo.security.entities.CredentialEntity;
    import com.example.demo.services.ExpenseService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.media.ArraySchema;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import org.springdoc.core.converters.models.PageableAsQueryParam;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.web.PagedResourcesAssembler;
    import org.springframework.hateoas.EntityModel;
    import org.springframework.hateoas.PagedModel;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.ErrorResponse;
    import org.springframework.web.bind.annotation.*;

    import java.util.NoSuchElementException;

    @Tag(name = "Expenses", description = "Operations related to user expenses")
    @RestController
    @RequestMapping("/expenses")
    public class ExpenseController {

        private final ExpenseService expenseService;
        private final ExpenseModelAssembler assembler;
        private final PagedResourcesAssembler<ExpenseResponseDTO> pagedResourcesAssembler;
        private final PagedResourcesAssembler<ExpenseResumeDTO> pagedResourcesAssemblerResume;
        private final ExpenseResumeModelAssembler resumeAssembler;


        @Autowired
        public ExpenseController(ExpenseService expenseService, ExpenseModelAssembler assembler, PagedResourcesAssembler<ExpenseResponseDTO> pagedResourcesAssembler,
                                 ExpenseResumeModelAssembler resumeAssembler,  PagedResourcesAssembler<ExpenseResumeDTO> pagedResourcesAssemblerResume) {
            this.expenseService = expenseService;
            this.assembler = assembler;
            this.pagedResourcesAssembler = pagedResourcesAssembler;
            this.resumeAssembler = resumeAssembler;
            this.pagedResourcesAssemblerResume = pagedResourcesAssemblerResume;


        }

        @Operation(
                summary = "Get all expenses",
                description = "Returns a list of all expenses registered in the system. You can optionally filter by category."
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Expense list successfully retrieved",
                        content = @Content(mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = ExpenseResponseDTO.class)))),

                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),

                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),

                @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),

                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PageableAsQueryParam
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
                summary = "Get all active expenses",
                description = "Retrieves a paginated list of all active expenses in the system."
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Active expenses retrieved successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_TODOS_GASTOS_ACTIVOS')")
        @GetMapping("/active")
        public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> getAllExpensesActive(
                Pageable pageable) {

            Page<ExpenseResponseDTO> expenses = expenseService.findAllActive(pageable);

            PagedModel<EntityModel<ExpenseResponseDTO>> model =
                    pagedResourcesAssembler.toModel(expenses, assembler);

            return ResponseEntity.ok(model);
        }

        @Operation(
                summary = "Get all inactive expenses",
                description = "Retrieves a paginated list of all inactive expenses in the system."
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_TODOS_GASTOS_INACTIVOS')")
        @GetMapping("/inactive")
        public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> getAllExpensesInactive(
                Pageable pageable) {

            Page<ExpenseResponseDTO> expenses;
            expenses = expenseService.findAllInactive(pageable);
            PagedModel<EntityModel<ExpenseResponseDTO>> model = pagedResourcesAssembler.toModel(expenses, assembler);

            return ResponseEntity.ok(model);
        }

        @Operation(
                summary = "Get an expense by ID for a specific user",
                description = "Returns a specific expense by its ID if it exists and belongs to the authenticated user.",
                parameters = {
                        @Parameter(name = "id", description = "ID of the expense to retrieve", required = true)
                }
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Expense found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResumeDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this resource",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Expense not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_GASTO')")
        @GetMapping("/{id}")
        public ResponseEntity<EntityModel<ExpenseResponseDTO>> getExpenseById(
                @PathVariable Long id,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long userId = credential.getUser().getId();

            boolean isAdmin = credential.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            ExpenseResponseDTO expense;

            if (isAdmin) {
                expense = expenseService.findById(id);
            } else {
                expense = expenseService.findByIdIfOwned(id, userId);
            }

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
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this resource",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "User not found or no expenses for user",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
        @GetMapping("/user/{userId}")
        public ResponseEntity<PagedModel<EntityModel<ExpenseResumeDTO>>> findByUserId(
                @PathVariable Long userId,
                @AuthenticationPrincipal CredentialEntity credential,
                @ModelAttribute ExpenseFilterDTO filters,
                Pageable pageable) {

            if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
                throw new OwnershipException("You do not have permission to access this resource.");
            }

            Page<ExpenseResumeDTO> expenses = expenseService.findByUserIdWithFilters(userId, filters, pageable);
            PagedModel<EntityModel<ExpenseResumeDTO>> model = pagedResourcesAssemblerResume.toModel(expenses, resumeAssembler);
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
                @ApiResponse(
                        responseCode = "201",
                        description = "Expense successfully created",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - Cannot create expense for this trip",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Trip or user not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                )
        })
        @PreAuthorize("hasAuthority('CREAR_GASTO')")
        @PostMapping
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
                @ApiResponse(
                        responseCode = "200",
                        description = "Expense successfully updated",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Expense not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)
                        )
                )
        })
        @PreAuthorize("hasAuthority('MODIFICAR_GASTO')")
        @PutMapping("/{id}")
        public ResponseEntity<ExpenseResumeDTO> updateExpense(
                @PathVariable Long id,
                @RequestBody @Valid ExpenseUpdateDTO dto,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long myUserId = credential.getUser().getId();
            expenseService.updateIfOwned(id, dto, myUserId);
            ExpenseResumeDTO updated = expenseService.findResumeById(id);
            return ResponseEntity.ok(updated);
        }


        @Operation(
                summary = "Delete an expense by ID",
                description = "Deletes the expense corresponding to the provided ID if it belongs to the authenticated user.",
                parameters = {
                        @Parameter(name = "id", description = "ID of the expense to delete", required = true)
                }
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Expense successfully deleted"),
                @ApiResponse(responseCode = "403", description = "Access denied"),
                @ApiResponse(responseCode = "404", description = "Expense not found")
        })
        @PreAuthorize("hasAuthority('ELIMINAR_GASTO')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteExpense(
                @PathVariable Long id,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long myUserId = credential.getUser().getId();
            expenseService.deleteIfOwned(id, myUserId);
            return ResponseEntity.noContent().build();
        }


        @Operation(
                summary = "Restore an expense",
                description = "Reactivates an expense that was previously deleted (soft-deleted) by setting its status to active."
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Expense restored successfully"),
                @ApiResponse(responseCode = "403", description = "Access denied"),
                @ApiResponse(responseCode = "404", description = "Expense not found")
        })
        @PreAuthorize("hasAuthority('RESTAURAR_GASTO')")
        @PutMapping("/restore/{id}")
        public ResponseEntity<Void> restoreExpense(
                @PathVariable Long id,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long myUserId = credential.getUser().getId();
            expenseService.restoreIfOwned(id, myUserId);
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
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Double.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_PROMEDIO_USUARIO')")
        @GetMapping("/averageByUserId/{userId}")
        public ResponseEntity<Double> getAverageExpensesByUser(
                @PathVariable Long userId,
                @AuthenticationPrincipal CredentialEntity credential) {

            if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
                throw new OwnershipException("You do not have permission to access this resource.");
            }

            return ResponseEntity.ok(expenseService.getAverageExpenseByUserId(userId));
        }


        @Operation(
                summary = "Get expenses by trip ID",
                description = "Retrieves all expenses associated with a specific trip ID, only if the trip belongs to the authenticated user.",
                parameters = {
                        @Parameter(name = "tripId", description = "ID of the trip", required = true)
                }
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExpenseResumeDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Access denied",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Trip not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_GASTOS_VIAJE')")
        @GetMapping("/trip/{tripId}")
        public ResponseEntity<PagedModel<EntityModel<ExpenseResumeDTO>>> getExpensesByTripId(
                @PathVariable Long tripId,
                @AuthenticationPrincipal CredentialEntity credential,
                Pageable pageable) {

            Long myUserId = credential.getUser().getId();
            Page<ExpenseResumeDTO> expenses = expenseService.findByTripIdIfOwned(tripId, myUserId, pageable);
            PagedModel<EntityModel<ExpenseResumeDTO>> model = pagedResourcesAssemblerResume.toModel(expenses, resumeAssembler);
            return ResponseEntity.ok(model);
        }


        @Operation(
                summary = "Get real average expense by user ID",
                description = "Calculates the real average amount a user must pay, considering shared expenses."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Average calculated successfully",
                        content = @Content(schema = @Schema(implementation = Double.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
        @GetMapping("/realAverageByUserId/{userId}")
        public ResponseEntity<Double> getRealAverageExpense(
                @PathVariable Long userId,
                @AuthenticationPrincipal CredentialEntity credential) {

            if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
                throw new OwnershipException("You do not have permission to access this resource.");
            }

            return ResponseEntity.ok(expenseService.getRealAverageExpenseByUser(userId));
        }

        @Operation(
                summary = "Get average expense by trip ID",
                description = "Returns the average amount of all expenses associated with a specific trip."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Average calculated successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Double.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Access denied",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Trip not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_PROMEDIO_VIAJE')")
        @GetMapping("/averageByTripId/{tripId}")
        public ResponseEntity<Double> getAverageExpensesByTrip(
                @PathVariable Long tripId,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long myUserId = credential.getUser().getId();
            Double average = expenseService.getAverageExpenseByTripIdIfOwned(tripId, myUserId);
            return ResponseEntity.ok(average);
        }


        @Operation(
                summary = "Get total expenses by trip ID",
                description = "Returns the total amount of all expenses registered for a specific trip."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Total calculated successfully",
                        content = @Content(schema = @Schema(implementation = Double.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Access denied",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Trip not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_TOTAL_GASTO_VIAJE')")
        @GetMapping("/totalByTripId/{tripId}")
        public ResponseEntity<Double> getTotalExpensesByTrip(
                @PathVariable Long tripId,
                @AuthenticationPrincipal CredentialEntity credential) {

            Long myUserId = credential.getUser().getId();
            Double total = expenseService.getTotalExpenseByTripIdIfOwned(tripId, myUserId);
            return ResponseEntity.ok(total);
        }


        @Operation(
                summary = "Get total real expenses by user ID",
                description = "Returns the total amount a user must pay, considering the expense is divided among participants."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Total calculated successfully",
                        content = @Content(schema = @Schema(implementation = Double.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PreAuthorize("hasAuthority('VER_GASTO_USUARIO')")
        @GetMapping("/realTotalByUserId/{userId}")
        public ResponseEntity<Double> getTotalRealExpensesByUser(
                @PathVariable Long userId,
                @AuthenticationPrincipal CredentialEntity credential) {

            if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
                throw new OwnershipException("You do not have permission to access this resource.");
            }

            return ResponseEntity.ok(expenseService.getTotalRealExpenseByUser(userId));
        }

        // hateoas
        @GetMapping("/trip/{tripId}/assembler-helper")
        public ResponseEntity<PagedModel<EntityModel<ExpenseResponseDTO>>> getExpensesByTripId(
                @PathVariable Long tripId,
                Pageable pageable) {
            throw new UnsupportedOperationException("Método solo utilizado para link building HATEOAS.");
        }
    }
