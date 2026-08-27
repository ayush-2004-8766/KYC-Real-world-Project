package com.example.KYC.project.exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}