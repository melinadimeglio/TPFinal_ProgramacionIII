package com.example.demo.DTOs.Review;

import com.example.demo.DTOs.Review.Response.ReviewResponseDTO;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityReviewSummaryDTO {
    private Double generalAvg;
    private Long totalReviews;
    private Double guideAvg;
    private Double puntualityAvg;
    private Double priceAvg;
    private Double securityAvg;
    private Map<Integer, Long> distribution;
    private Boolean userYaReseno;
    private List<ReviewResponseDTO> reviews;
}
