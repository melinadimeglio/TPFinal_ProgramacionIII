package com.example.demo.security.entities;


import com.example.demo.security.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "Rol")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role role;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permits",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permit_id"))
    private Set<PermitEntity> permits = new HashSet<>();

    public RoleEntity(Role name) {
        this.role = name;
    }

    public void addPermit(PermitEntity permit) {
        this.permits.add(permit);
    }
}
