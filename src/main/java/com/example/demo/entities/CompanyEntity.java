package com.example.demo.entities;
import com.example.demo.security.entities.CredentialEntity;
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

    private String username;

    private String description;

    private String phone;

    @Column(unique = true)
    private String taxId;

    private String location;

    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<ActivityEntity> activities;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private CredentialEntity credential;

}
