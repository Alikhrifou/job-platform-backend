package com.ali.jobmatch.controller;

import com.ali.jobmatch.dto.request.StudentProfileRequest;
import com.ali.jobmatch.dto.response.StudentProfileResponse;
import com.ali.jobmatch.service.FileStorageService;
import com.ali.jobmatch.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student profile management endpoints")
@SecurityRequirement(name = "Bearer Token")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/profile")
    @Operation(summary = "Create or update student profile")
    public ResponseEntity<StudentProfileResponse> createOrUpdateProfile(
            @Valid @RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(studentService.createOrUpdateProfile(request));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current student profile")
    public ResponseEntity<StudentProfileResponse> getMyProfile() {
        return ResponseEntity.ok(studentService.getMyProfile());
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student profile by ID")
    public ResponseEntity<StudentProfileResponse> getProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getProfile(studentId));
    }

    @PostMapping("/profile/resume")
    @Operation(summary = "Upload resume (PDF or DOCX, max 5MB)")
    public ResponseEntity<Map<String, String>> uploadResume(@RequestParam("file") MultipartFile file) {
        String filename = studentService.uploadResume(file);
        return ResponseEntity.ok(Map.of(
                "resumeUrl", filename,
                "resumeOriginalName", file.getOriginalFilename() != null ? file.getOriginalFilename() : filename
        ));
    }

    @DeleteMapping("/profile/resume")
    @Operation(summary = "Delete uploaded resume")
    public ResponseEntity<Void> deleteResume() {
        studentService.deleteResume();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resume/{filename}")
    @Operation(summary = "Download/view a student resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable String filename) {
        Resource resource = fileStorageService.loadResume(filename);

        String contentType = "application/octet-stream";
        if (filename.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (filename.endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (filename.endsWith(".doc")) {
            contentType = "application/msword";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
