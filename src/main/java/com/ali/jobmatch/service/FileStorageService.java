package com.ali.jobmatch.service;

import com.ali.jobmatch.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Value("${file.upload-dir:uploads/resumes}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeResume(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only PDF and DOCX files are allowed");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String storedFilename = "resume_" + userId + "_" + UUID.randomUUID() + extension;

        try {
            // Delete old resumes for this user
            Files.list(uploadPath)
                    .filter(p -> p.getFileName().toString().startsWith("resume_" + userId + "_"))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });

            Path targetLocation = uploadPath.resolve(storedFilename).normalize();
            if (!targetLocation.startsWith(uploadPath)) {
                throw new BadRequestException("Invalid file path");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    public Resource loadResume(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(uploadPath)) {
                throw new BadRequestException("Invalid file path");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new BadRequestException("Resume file not found");
        } catch (MalformedURLException e) {
            throw new BadRequestException("Resume file not found");
        }
    }

    public void deleteResume(String filename) {
        if (filename == null || filename.isBlank()) return;
        // Skip old external URLs that aren't local filenames
        if (filename.startsWith("http://") || filename.startsWith("https://")) return;
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            if (filePath.startsWith(uploadPath)) {
                Files.deleteIfExists(filePath);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Extracts plain text from an uploaded resume file (PDF or DOCX).
     * Returns an empty string if the file is missing, unreadable, or an unsupported format.
     */
    public String readResumeText(String filename) {
        if (filename == null || filename.isBlank()) return "";
        if (filename.startsWith("http://") || filename.startsWith("https://")) return "";

        Path filePath = uploadPath.resolve(filename).normalize();
        if (!filePath.startsWith(uploadPath) || !Files.exists(filePath)) return "";

        String lower = filename.toLowerCase();
        try (InputStream in = Files.newInputStream(filePath)) {
            if (lower.endsWith(".pdf")) {
                try (PDDocument doc = Loader.loadPDF(Files.readAllBytes(filePath))) {
                    return new PDFTextStripper().getText(doc);
                }
            } else if (lower.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(in)) {
                    return doc.getParagraphs().stream()
                            .map(XWPFParagraph::getText)
                            .collect(Collectors.joining(" "));
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}
