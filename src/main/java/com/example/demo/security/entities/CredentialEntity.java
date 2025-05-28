package com.example.demo.security.entities;

import com.example.demo.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Credential")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class CredentialEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;


    @ManyToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinTable(
            name = "credentials_roles",
            joinColumns = @JoinColumn(name = "credential_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(rol -> authorities.add(
                new SimpleGrantedAuthority(rol.getRole().name())));
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
