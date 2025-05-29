package com.example.demo.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnumValue (HttpMessageNotReadableException e){
        if (e.getCause() instanceof InvalidFormatException){
            InvalidFormatException cause = (InvalidFormatException) e.getCause();
            if (cause.getTargetType().isEnum()){
                String enumName = cause.getTargetType().getSimpleName();
                String invalidValue = cause.getValue().toString();
                return ResponseEntity.badRequest().body("Valor invalido para " + enumName + ": " + invalidValue);
            }
        }

        return ResponseEntity.badRequest().body("Error en el formato del cuerpo del request.");
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
    public ResponseEntity<String> handleRepitedElementExceptions (RepitedElementException e){
        String message = e.getMessage() != null ? e.getMessage() : "El elemento ya existe y debe ser único.";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

}
