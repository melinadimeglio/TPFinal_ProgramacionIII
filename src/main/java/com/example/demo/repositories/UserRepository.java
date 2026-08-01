package com.example.demo.repositories;

import com.example.demo.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByDni(String dni);

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findAllByActiveTrue(Pageable pageable);

    Page<UserEntity> findAllByActiveFalse(Pageable pageable);

    Page<UserEntity> findByUsernameContainingIgnoreCaseAndActiveTrue(String username, Pageable pageable);

    @Query("SELECT u FROM UserEntity u JOIN u.credential c JOIN c.roles r WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) AND u.active = true AND r.role <> 'ROLE_ADMIN' AND r.role <> 'ROLE_COMPANY'")
    Page<UserEntity> searchByUsernameExcludingAdminAndCompany(@Param("username") String username, Pageable pageable);

}
