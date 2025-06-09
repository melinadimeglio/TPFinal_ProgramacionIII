package com.example.demo.security;

import com.example.demo.security.DTO.AuthRequest;
import com.example.demo.security.DTO.AuthResponse;
import com.example.demo.security.DTO.RefreshTokenRequest;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.repositories.CredentialRepository;
import com.example.demo.security.services.AuthService;
import com.example.demo.security.services.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;
    private final CredentialRepository credentialRepository;

    public AuthController(AuthService authService, JWTService
            jwtService, CredentialRepository credentialRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.credentialRepository = credentialRepository;
    }

    @PostMapping()
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody
                                                         AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        CredentialEntity credential = credentialRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Credential not found."));

        credential.setRefreshToken(refreshToken);
        credentialRepository.save(credential);

        return ResponseEntity.ok(new AuthResponse(token, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody
                                                     RefreshTokenRequest request){
        AuthResponse response =
                authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

}

