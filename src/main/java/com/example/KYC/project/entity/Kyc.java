package com.example.KYC.project.entity;

import com.example.KYC.project.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kyc")
public class Kyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, unique = true)
    private String panNumber;

    @Column(nullable = false)
    private String aadhaarNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pincode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status = KycStatus.PENDING;

    private LocalDateTime submittedAt;

    private LocalDateTime verifiedAt;

    private String rejectionReason;


}