package com.example.demo.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidEnumValue (HttpMessageNotReadableException e){
        if (e.getCause() instanceof InvalidFormatException){
            InvalidFormatException cause = (InvalidFormatException) e.getCause();
            if (cause.getTargetType().isEnum()){
                String enumName = cause.getTargetType().getSimpleName();
                String invalidValue = cause.getValue().toString();
                return buildResponse(HttpStatus.BAD_REQUEST, "Valor invalido para " + enumName + ": " + invalidValue);
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, "Error en el formato del cuerpo del request." + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions (MethodArgumentNotValidException e){
        Map<String, String> errores = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();
                errores.put(fieldName, errorMessage);
            } else {
                errores.put("Error", error.getDefaultMessage());
            }
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(RepitedElementException.class)
    public ResponseEntity<Object> handleRepitedElementExceptions (RepitedElementException e){
        String message = e.getMessage() != null ? e.getMessage() : "El elemento ya existe y debe ser único.";
        return buildResponse(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException e) {
        String error = e.getMessage() != null ? e.getMessage() : "El recurso no fue encontrado.";

        return buildResponse(HttpStatus.NOT_FOUND, error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Violación de restricción: " + e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParameter(MissingServletRequestParameterException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Falta el parámetro requerido: " + e.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para este endpoint.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Usuario no encontrado: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso." + ex.getMessage());
    }

    @ExceptionHandler(MPApiException.class)
    public ResponseEntity<Object> handleMPApiException(MPApiException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Error al procesar el pago." + ex.getMessage());
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<Object> handleInvalidReservation(ReservationException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MPException.class)
    public ResponseEntity<Object> handleMPException(MPException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Error al procesar el pago." + ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado: " + ex.getContentType());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
