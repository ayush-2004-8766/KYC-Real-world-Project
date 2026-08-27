package com.example.KYC.project.repository;

import com.example.KYC.project.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KycDocumentRepository
        extends JpaRepository<KycDocument, Long> {

    List<KycDocument> findByKycId(Long kycId);
}