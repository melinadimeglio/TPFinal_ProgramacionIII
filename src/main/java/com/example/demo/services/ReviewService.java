package com.example.demo.services;

import com.example.demo.DTOs.Review.ActivityReviewSummaryDTO;
import com.example.demo.DTOs.Review.Request.ReviewRequestDTO;
import com.example.demo.DTOs.Review.Response.ReviewResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ReviewEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ReservationRepository;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        ActivityEntity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));


        boolean reservationCompleted = reservationRepository
                .existsByUserIdAndActivity_IdAndActive(userId, dto.getActivityId(), false);

        if (!reservationCompleted) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo podés reseñar actividades que hayas completado"
            );
        }

        if (reviewRepository.existsByUserIdAndActivityId(userId, dto.getActivityId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya dejaste una reseña para esta actividad"
            );
        }

        ReviewEntity review = new ReviewEntity();
        review.setUser(user);
        review.setActivity(activity);
        review.setRating(dto.getRating());
        review.setTitulo(dto.getTitle());
        review.setComentario(dto.getComentary());
        review.setRatingGuia(dto.getRatingGuide());
        review.setRatingPuntualidad(dto.getRatingPuntuality());
        review.setRatingPrecio(dto.getRatingPrice());
        review.setRatingSeguridad(dto.getRatingSecurity());

        ReviewEntity saved = reviewRepository.save(review);
        return new ReviewResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public ActivityReviewSummaryDTO getSummaryByActivity(Long activityId, Long userId) {
        activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));

        List<ReviewEntity> reviews = reviewRepository.findByActivityIdOrderByFechaCreacionDesc(activityId);

        ActivityReviewSummaryDTO summary = new ActivityReviewSummaryDTO();

        double promedio = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
        summary.setGeneralAvg(Math.round(promedio * 10.0) / 10.0);
        summary.setTotalReviews((long) reviews.size());

        summary.setGuideAvg(roundAvg(reviews.stream().mapToInt(ReviewEntity::getRatingGuia).average().orElse(0)));
        summary.setPuntualityAvg(roundAvg(reviews.stream().mapToInt(ReviewEntity::getRatingPuntualidad).average().orElse(0)));
        summary.setPriceAvg(roundAvg(reviews.stream().mapToInt(ReviewEntity::getRatingPrecio).average().orElse(0)));
        summary.setSecurityAvg(roundAvg(reviews.stream().mapToInt(ReviewEntity::getRatingSeguridad).average().orElse(0)));

        Map<Integer, Long> distribucion = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int star = i;
            distribucion.put(star, reviews.stream().filter(r -> r.getRating() == star).count());
        }
        summary.setDistribution(distribucion);

        summary.setUserYaReseno(
                userId != null && reviewRepository.existsByUserIdAndActivityId(userId, activityId)
        );

        summary.setReviews(reviews.stream()
                .map(ReviewResponseDTO::new)
                .collect(Collectors.toList()));

        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSimpleAverage(Long activityId) {
        Double average = reviewRepository.findAverageRatingByActivityId(activityId);
        Long total = reviewRepository.countByActivityId(activityId);

        Map<String, Object> result = new HashMap<>();
        result.put("average", Math.round((average != null ? average : 0.0) * 10.0) / 10.0);
        result.put("total", total != null ? total : 0L);
        return result;
    }

    private double roundAvg(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}
