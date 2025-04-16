package com.snapscreen.snapscreen_api.controller;

import com.snapscreen.snapscreen_api.service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final S3StorageService s3StorageService;

    @Autowired
    public ResumeController(S3StorageService s3StorageService) {
        this.s3StorageService = s3StorageService;
    }

    /**
     * Upload a resume file for a user (replaces any existing resume)
     * @param file The resume file to upload
     * @return Object with the S3 object key and a pre-signed URL for the uploaded file
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        String userId = (String) authentication.getPrincipal();
        
        try {
            // Validate the file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            
            // Check file type
            String fileExtension = getFileExtension(file.getOriginalFilename());
            if (!isValidResumeFileType(fileExtension)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid file type. Supported types: PDF, DOC, DOCX, TXT"
                ));
            }
            
            // Upload the file (this will replace any existing resume)
            String objectKey = s3StorageService.uploadResume(userId, file);
            
            // Get a pre-signed URL (valid for 60 minutes)
            String presignedUrl = s3StorageService.getPreSignedUrl(objectKey, 60);
            
            // Return the object key and URL
            Map<String, String> response = new HashMap<>();
            response.put("objectKey", objectKey);
            if (presignedUrl != null) {
                response.put("url", presignedUrl);
            }
            response.put("filename", file.getOriginalFilename());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
    
    /**
     * Get the current user's resume
     * @return The resume information if it exists
     */
    @GetMapping
    public ResponseEntity<?> getUserResume(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        
        Optional<String> objectKeyOpt = s3StorageService.getUserResume(userId);
        
        if (objectKeyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        String objectKey = objectKeyOpt.get();
        String presignedUrl = s3StorageService.getPreSignedUrl(objectKey, 60);
        String filename = objectKey.substring(objectKey.lastIndexOf('/') + 1);
        
        Map<String, String> result = new HashMap<>();
        result.put("objectKey", objectKey);
        if (presignedUrl != null) {
            result.put("url", presignedUrl);
        }
        result.put("filename", filename);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Check if the current user has a resume
     * @return Status indicating if the user has a resume
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> hasResume(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        boolean hasResume = s3StorageService.hasResume(userId);
        return ResponseEntity.ok(Map.of("hasResume", hasResume));
    }
    
    /**
     * Get a pre-signed URL for a resume
     * @param objectKey The S3 object key of the resume
     * @return A pre-signed URL for accessing the resume
     */
    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getResumeUrl(@RequestParam String objectKey) {
        String presignedUrl = s3StorageService.getPreSignedUrl(objectKey, 60);
        if (presignedUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to generate pre-signed URL"));
        }
        Map<String, String> result = new HashMap<>();
        result.put("url", presignedUrl);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Delete the current user's resume
     * @return Empty response with OK status
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUserResume(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        s3StorageService.deleteExistingResume(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get the file extension from a filename
     * @param filename The filename
     * @return The file extension (including the dot)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
    
    /**
     * Check if the file type is valid for resumes
     * @param fileExtension The file extension (including the dot)
     * @return True if the file type is valid, false otherwise
     */
    private boolean isValidResumeFileType(String fileExtension) {
        return fileExtension.equals(".pdf") || 
               fileExtension.equals(".doc") || 
               fileExtension.equals(".docx") || 
               fileExtension.equals(".txt");
    }
} 