package com.example.demo.DTOs.Review.Response;

import com.example.demo.entities.ReviewEntity;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long id;
    private String username;
    private String userAvatar;
    private Integer rating;
    private String title;
    private String comentary;
    private Integer ratingGuide;
    private Integer ratingPuntuality;
    private Integer ratingPrice;
    private Integer ratingSecurity;
    private String creationDate;

    public ReviewResponseDTO(ReviewEntity review) {
        this.id = review.getId();
        this.username = review.getUser().getUsername();
        this.userAvatar = buildAvatar(review.getUser().getUsername());
        this.rating = review.getRating();
        this.title = review.getTitulo();
        this.comentary = review.getComentario();
        this.ratingGuide = review.getRatingGuia();
        this.ratingPuntuality = review.getRatingPuntualidad();
        this.ratingPrice = review.getRatingPrecio();
        this.ratingSecurity = review.getRatingSeguridad();
        this.creationDate = review.getFechaCreacion()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private String buildAvatar(String username) {
        if (username == null || username.isBlank()) return "??";
        String[] parts = username.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return username.substring(0, Math.min(2, username.length())).toUpperCase();
    }
}
