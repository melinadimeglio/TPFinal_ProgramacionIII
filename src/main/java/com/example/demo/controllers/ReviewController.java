package com.example.demo.controllers;

import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
import com.example.demo.DTOs.Review.ActivityReviewSummaryDTO;
import com.example.demo.DTOs.Review.Request.ReviewRequestDTO;
import com.example.demo.DTOs.Review.Response.ReviewResponseDTO;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Operations related to activity reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(
            summary = "Create a new review",
            description = "Allows a user to create a review for an activity. Only allowed if the user has a COMPLETED reservation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Review successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user has no completed reservation",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - user already reviewed this activity",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('CREAR_RESEÑA')")
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @AuthenticationPrincipal CredentialEntity credential,
            @Valid @RequestBody ReviewRequestDTO dto) {

        if (credential.getUser() == null) {
            throw new OwnershipException("Only users (not companies) can create reviews.");
        }

        Long userId = credential.getUser().getId();
        ReviewResponseDTO response = reviewService.createReview(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get review summary by activity",
            description = "Returns full review summary including averages, distribution, and list of reviews."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Summary retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityReviewSummaryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<ActivityReviewSummaryDTO> getSummaryByActivity(
            @PathVariable Long activityId,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = null;

        if (credential != null && credential.getUser() != null) {
            userId = credential.getUser().getId();
        }

        ActivityReviewSummaryDTO summary =
                reviewService.getSummaryByActivity(activityId, userId);

        return ResponseEntity.ok(summary);
    }

    @Operation(
            summary = "Get simple average rating",
            description = "Returns only average rating and total number of reviews. Lightweight endpoint for activity listings."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Average retrieved successfully"
            )
    })
    @GetMapping("/activity/{activityId}/avg")
    public ResponseEntity<Map<String, Object>> getSimpleAverage(
            @PathVariable Long activityId) {

        return ResponseEntity.ok(reviewService.getSimpleAverage(activityId));
    }
}
