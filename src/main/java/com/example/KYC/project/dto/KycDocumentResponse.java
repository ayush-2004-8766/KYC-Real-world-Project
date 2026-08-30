package com.example.KYC.project.dto;

import com.example.KYC.project.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentResponse {

    private Long id;

    private DocumentType documentType;

    private String fileName;

    private String documentUrl;

    private LocalDateTime uploadedAt;



}