package com.example.KYC.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycRejectRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;


}