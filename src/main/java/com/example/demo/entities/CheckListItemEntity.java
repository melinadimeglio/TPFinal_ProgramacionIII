package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "CheckListItem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CheckListItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripcion del item no puede estar vacia.")
    private String description;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private CheckListEntity checklist;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
