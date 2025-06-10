package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
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

    private String description;
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private CheckListEntity checklist;

    @Builder.Default
    private boolean active = true;
}
