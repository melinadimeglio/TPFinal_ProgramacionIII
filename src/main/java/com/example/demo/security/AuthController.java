package com.example.demo.security;

import com.example.demo.security.DTO.AuthRequest;
import com.example.demo.security.DTO.AuthResponse;
import com.example.demo.security.DTO.RefreshTokenRequest;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.repositories.CredentialRepository;
import com.example.demo.security.services.AuthService;
import com.example.demo.security.services.JWTService;
import com.example.demo.security.services.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to authentication, token management and user sessions")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;
    private final CredentialRepository credentialRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthService authService, JWTService
            jwtService, CredentialRepository credentialRepository, TokenBlacklistService tokenBlacklistService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.credentialRepository = credentialRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email and password, and returns a JWT access token and a refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })

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

    @Operation(
            summary = "Refresh access token",
            description = "Renews the access token using a valid refresh token. Returns a new access token and refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody
                                                     RefreshTokenRequest request){
        AuthResponse response =
                authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the user by revoking the current access token. Adds the token to a blacklist."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "400", description = "No valid token provided")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest){
        String auth = httpServletRequest.getHeader("Authorization");
        if(auth != null && auth.startsWith("Bearer")){
            String token = auth.substring(7);
            tokenBlacklistService.blacklist(token);
            return ResponseEntity.ok("Logged out successfully.");
        }
        return ResponseEntity.badRequest().body("No valid token provided.");
    }



}

