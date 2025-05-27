package com.example.demo.security;

import com.example.demo.security.DTO.AuthRequest;
import com.example.demo.security.DTO.AuthResponse;
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
    public AuthController(AuthService authService, JWTService
            jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }
    @PostMapping()
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody
                                                         AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

