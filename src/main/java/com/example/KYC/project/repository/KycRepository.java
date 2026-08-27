package com.example.KYC.project.repository;

import com.example.KYC.project.entity.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycRepository extends JpaRepository<Kyc, Long> {

    Optional<Kyc> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}