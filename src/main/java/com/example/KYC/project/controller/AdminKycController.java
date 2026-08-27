package com.example.KYC.project.controller;

import com.example.KYC.project.dto.KycRejectRequest;
import com.example.KYC.project.dto.KycResponse;
import com.example.KYC.project.service.KycService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/admin/kyc")
public class AdminKycController {

    private final KycService kycService;

    public AdminKycController(KycService kycService) {
        this.kycService = kycService;
    }


    @GetMapping
    public ResponseEntity<List<KycResponse>> getAllKyc() {

        return ResponseEntity.ok(
                kycService.getAllKyc()
        );
    }


    @PutMapping("/{kycId}/approve")
    public ResponseEntity<String> approveKyc(
            @PathVariable Long kycId) {

        return ResponseEntity.ok(
                kycService.approveKyc(kycId)
        );
    }


    @PutMapping("/{kycId}/reject")
    public ResponseEntity<String> rejectKyc(
            @PathVariable Long kycId,
            @Valid @RequestBody KycRejectRequest request) {

        return ResponseEntity.ok(
                kycService.rejectKyc(
                        kycId,
                        request.getReason()
                )
        );
    }
}