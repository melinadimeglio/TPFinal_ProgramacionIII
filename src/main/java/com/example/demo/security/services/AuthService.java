package com.example.demo.security.services;

import com.example.demo.security.DTO.AuthRequest;
import com.example.demo.security.DTO.AuthResponse;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.repositories.CredentialRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CredentialRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(CredentialRepository credentialsRepository,
                       AuthenticationManager authenticationManager, JWTService jwtService) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );
        return credentialsRepository.findByEmail(input.username()).orElseThrow();
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        CredentialEntity user =
                credentialsRepository.findByEmail(username)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw  new IllegalArgumentException("Refresh token does not match");
        }
        if (!jwtService.validateRefreshToken(refreshToken, user)) {
            throw new IllegalArgumentException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        credentialsRepository.save(user);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
