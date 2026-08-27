package com.example.KYC.project.dto;

import com.example.KYC.project.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String panNumber;
    private String aadhaarNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private KycStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String rejectionReason;

}