package com.example.KYC.project.controller;

import com.example.KYC.project.dto.KycRequest;
import com.example.KYC.project.dto.KycResponse;
import com.example.KYC.project.enums.DocumentType;
import com.example.KYC.project.service.KycService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }


    @PostMapping
    public ResponseEntity<KycResponse> submitKyc(
            Authentication authentication,
            @Valid @RequestBody KycRequest request) {

        return ResponseEntity.ok(
                kycService.submitKyc(
                        authentication.getName(),
                        request
                )
        );
    }


    @GetMapping("/my")
    public ResponseEntity<KycResponse> getMyKyc(
            Authentication authentication) {

        return ResponseEntity.ok(
                kycService.getMyKyc(
                        authentication.getName()
                )
        );
    }


    @PostMapping(
            value = "/{kycId}/documents",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<String> uploadDocument(

            Authentication authentication,

            @PathVariable Long kycId,

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("documentType")
            DocumentType documentType) {

        return ResponseEntity.ok(
                kycService.uploadDocument(
                        authentication.getName(),
                        kycId,
                        file,
                        documentType
                )
        );
    }
}