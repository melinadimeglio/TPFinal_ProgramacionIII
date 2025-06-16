package com.example.demo.security.filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage = switch (authException) {
            case BadCredentialsException badCredentialsException ->
                    "Invalid credentials";
            case DisabledException disabledException ->
                    "Account disabled";
            case LockedException lockedException ->
                    "Locked account";
            case AccountExpiredException accountExpiredException ->
                    "Expired account";
            case CredentialsExpiredException credentialsExpiredException
                    ->
                    "Expired credentials";
            case InsufficientAuthenticationException
                         insufficientAuthenticationException ->
                    "Insufficient authentication";
            case AuthenticationServiceException
                         authenticationServiceException ->
                    "Authentication service error";
            default -> "Authentication error: " +
                    authException.getMessage();
        };
        String jsonResponse = String.format("{\"error\": \"%s\",\"status\": %d, \"path\": \"%s\"}",
                errorMessage,
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getRequestURI());
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

}
