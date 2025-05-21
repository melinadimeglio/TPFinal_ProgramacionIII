package com.example.demo.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    private String password;
    private String description;

    @NotBlank
    private String phone;

    @NotBlank
    @Column(unique = true)
    private String tax_id;

    @NotBlank
    private String location;

    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<ActivityEntity> activities;

}
