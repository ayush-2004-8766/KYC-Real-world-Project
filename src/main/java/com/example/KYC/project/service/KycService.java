package com.example.KYC.project.service;

import com.example.KYC.project.dto.KycDocumentResponse;
import com.example.KYC.project.dto.KycRequest;
import com.example.KYC.project.dto.KycResponse;
import com.example.KYC.project.entity.Kyc;
import com.example.KYC.project.entity.KycDocument;
import com.example.KYC.project.entity.User;
import com.example.KYC.project.enums.DocumentType;
import com.example.KYC.project.enums.KycStatus;
import com.example.KYC.project.exception.BadRequestException;
import com.example.KYC.project.exception.ResourceNotFoundException;
import com.example.KYC.project.repository.KycDocumentRepository;
import com.example.KYC.project.repository.KycRepository;
import com.example.KYC.project.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class KycService {

    private final UserRepository userRepository;
    private final KycRepository kycRepository;
    private final KycDocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public KycService(
            UserRepository userRepository,
            KycRepository kycRepository,
            KycDocumentRepository documentRepository) {

        this.userRepository = userRepository;
        this.kycRepository = kycRepository;
        this.documentRepository = documentRepository;
    }


    // =========================
    // USER SUBMIT KYC
    // =========================

    @Transactional
    public KycResponse submitKyc(
            String email,
            KycRequest request) {

        User user = getUser(email);

        if (kycRepository.existsByUserId(user.getId())) {

            throw new BadRequestException(
                    "KYC already exists"
            );
        }

        Kyc kyc = new Kyc();

        kyc.setUser(user);
        kyc.setFullName(request.getFullName());
        kyc.setDob(request.getDob());
        kyc.setGender(request.getGender());
        kyc.setPanNumber(request.getPanNumber());
        kyc.setAadhaarNumber(request.getAadhaarNumber());
        kyc.setAddress(request.getAddress());
        kyc.setCity(request.getCity());
        kyc.setState(request.getState());
        kyc.setPincode(request.getPincode());

        kyc.setStatus(KycStatus.PENDING);
        kyc.setSubmittedAt(LocalDateTime.now());

        Kyc savedKyc = kycRepository.save(kyc);

        user.setKycStatus(KycStatus.PENDING);

        userRepository.save(user);

        return convertToResponse(savedKyc);
    }


    // =========================
    // GET MY KYC
    // =========================

    public KycResponse getMyKyc(String email) {

        User user = getUser(email);

        Kyc kyc = kycRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "KYC not found"
                        )
                );

        return convertToResponse(kyc);
    }


    // =========================
    // UPLOAD DOCUMENT
    // =========================

    @Transactional
    public String uploadDocument(
            String email,
            Long kycId,
            MultipartFile file,
            DocumentType documentType) {

        User user = getUser(email);

        Kyc kyc = kycRepository
                .findById(kycId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "KYC not found"
                        )
                );


        // SECURITY CHECK

        if (!kyc.getUser().getId().equals(user.getId())) {

            throw new BadRequestException(
                    "You cannot upload document for another user's KYC"
            );
        }


        // APPROVED KYC CANNOT BE MODIFIED

        if (kyc.getStatus() == KycStatus.APPROVED) {

            throw new BadRequestException(
                    "Approved KYC cannot be modified"
            );
        }


        // FILE NULL CHECK

        if (file == null || file.isEmpty()) {

            throw new BadRequestException(
                    "File is empty"
            );
        }


        // FILE SIZE CHECK

        if (file.getSize() > 5 * 1024 * 1024) {

            throw new BadRequestException(
                    "File size cannot exceed 5 MB"
            );
        }


        // DOCUMENT TYPE CHECK

        if (documentType == null) {

            throw new BadRequestException(
                    "Document type is required"
            );
        }


        // MIME TYPE CHECK

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equalsIgnoreCase("application/pdf")
                        || contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png"))) {

            throw new BadRequestException(
                    "Only PDF, JPG, JPEG and PNG files are allowed"
            );
        }


        try {

            Path directory =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(directory);


            // EXTENSION CHECK

            String extension =
                    getExtension(
                            file.getOriginalFilename()
                    );


            // UNIQUE FILE NAME

            String uniqueFileName =
                    UUID.randomUUID() + extension;


            Path target =
                    directory
                            .resolve(uniqueFileName)
                            .normalize();


            // PATH TRAVERSAL PROTECTION

            if (!target.getParent().equals(directory)) {

                throw new BadRequestException(
                        "Invalid file name"
                );
            }


            // SAVE FILE

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // SAVE DATABASE RECORD

            KycDocument document =
                    new KycDocument();

            document.setKyc(kyc);

            document.setDocumentType(
                    documentType
            );

            document.setFileName(
                    file.getOriginalFilename()
            );

            document.setFilePath(uniqueFileName);

//            document.setFilePath(
//                    target.toString()
//            );

            document.setUploadedAt(
                    LocalDateTime.now()
            );

            documentRepository.save(document);


            return "Document uploaded successfully";


        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload document"
            );
        }
    }
    // =========================
    // GET DOCUMENT
   // =========================

    public KycDocument getDocument(Long documentId) {

        return documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found"
                        )
                );
    }


// =========================
// GET DOCUMENT AS BYTE[]
// =========================

    public byte[] getDocumentBytes(Long documentId)
            throws IOException {

        KycDocument document =
                getDocument(documentId);


        Path path = Paths.get(uploadDir)
                .resolve(document.getFilePath());


//        Path path =
//                Paths.get(
//                        document.getFilePath()
//                );

        if (!Files.exists(path)) {

            throw new ResourceNotFoundException(
                    "File not found in folder"
            );
        }

        return Files.readAllBytes(path);
    }


    // =========================
    // ADMIN GET ALL KYC
    // =========================

    public List<KycResponse> getAllKyc() {

        return kycRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================
    // ADMIN APPROVE KYC
    // =========================

    @Transactional
    public String approveKyc(Long kycId) {

        Kyc kyc = kycRepository
                .findById(kycId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "KYC not found"
                        )
                );


        if (kyc.getStatus() == KycStatus.APPROVED) {

            throw new BadRequestException(
                    "KYC is already approved"
            );
        }


        kyc.setStatus(
                KycStatus.APPROVED
        );

        kyc.setVerifiedAt(
                LocalDateTime.now()
        );

        kyc.setRejectionReason(null);


        User user = kyc.getUser();

        user.setKycStatus(
                KycStatus.APPROVED
        );

        user.setLastKycDate(
                LocalDateTime.now()
        );


        kycRepository.save(kyc);

        userRepository.save(user);


        return "KYC approved successfully";
    }


    // =========================
    // ADMIN REJECT KYC
    // =========================

    @Transactional
    public String rejectKyc(
            Long kycId,
            String reason) {

        Kyc kyc = kycRepository
                .findById(kycId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "KYC not found"
                        )
                );


        if (kyc.getStatus() == KycStatus.APPROVED) {

            throw new BadRequestException(
                    "Approved KYC cannot be rejected"
            );
        }


        if (reason == null ||
                reason.isBlank()) {

            throw new BadRequestException(
                    "Rejection reason is required"
            );
        }


        kyc.setStatus(
                KycStatus.REJECTED
        );

        kyc.setRejectionReason(
                reason
        );

        kyc.setVerifiedAt(null);


        User user = kyc.getUser();

        user.setKycStatus(
                KycStatus.REJECTED
        );


        kycRepository.save(kyc);

        userRepository.save(user);


        return "KYC rejected successfully";
    }


    // =========================
    // GET USER
    // =========================

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );
    }


    // =========================
    // FILE EXTENSION
    // =========================

    private String getExtension(
            String fileName) {

        if (fileName == null ||
                fileName.isBlank()) {

            throw new BadRequestException(
                    "Invalid file name"
            );
        }


        String lowerCaseName =
                fileName.toLowerCase();


        if (lowerCaseName.endsWith(".pdf")) {
            return ".pdf";
        }

        if (lowerCaseName.endsWith(".jpg")) {
            return ".jpg";
        }

        if (lowerCaseName.endsWith(".jpeg")) {
            return ".jpeg";
        }

        if (lowerCaseName.endsWith(".png")) {
            return ".png";
        }


        throw new BadRequestException(
                "Only PDF, JPG, JPEG and PNG files are allowed"
        );
    }


    // =========================
    // RESPONSE CONVERTER
    // =========================

    private KycResponse convertToResponse(
            Kyc kyc) {

        KycResponse response =
                new KycResponse();

        response.setId(kyc.getId());

        response.setUserId(
                kyc.getUser().getId()
        );

        response.setFullName(
                kyc.getFullName()
        );

        response.setDob(
                kyc.getDob()
        );

        response.setGender(
                kyc.getGender()
        );

        response.setPanNumber(
                kyc.getPanNumber()
        );

        response.setAadhaarNumber(
                kyc.getAadhaarNumber()
        );

        response.setAddress(
                kyc.getAddress()
        );

        response.setCity(
                kyc.getCity()
        );

        response.setState(
                kyc.getState()
        );

        response.setPincode(
                kyc.getPincode()
        );

        response.setStatus(
                kyc.getStatus()
        );

        response.setSubmittedAt(
                kyc.getSubmittedAt()
        );

        response.setVerifiedAt(
                kyc.getVerifiedAt()
        );

        response.setRejectionReason(
                kyc.getRejectionReason()
        );

        // Documents
        List<KycDocument> documents =
                documentRepository.findByKycId(
                        kyc.getId()
                );

        List<KycDocumentResponse> documentResponses =
                documents.stream()
                        .map(document -> {

                            KycDocumentResponse docResponse =
                                    new KycDocumentResponse();

                            docResponse.setId(
                                    document.getId()
                            );

                            docResponse.setDocumentType(
                                    document.getDocumentType()
                            );

                            docResponse.setFileName(
                                    document.getFileName()
                            );

                            docResponse.setDocumentUrl(
                                    "/api/kyc/documents/"
                                            + document.getId()
                            );

                            docResponse.setUploadedAt(
                                    document.getUploadedAt()
                            );

                            return docResponse;

                        })
                        .toList();

        response.setDocuments(
                documentResponses
        );

        return response;
    }
}