package com.example.KYC.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
    handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors =
                new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .badRequest()
                .body(errors);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>>
    handleNotFound(
            ResourceNotFoundException exception) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>>
    handleBadRequest(
            BadRequestException exception) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                exception.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>>
    handleBadCredentials() {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                "Invalid email or password"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>>
    handleGeneral(Exception exception) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}