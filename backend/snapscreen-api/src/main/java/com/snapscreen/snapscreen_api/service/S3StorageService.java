package com.snapscreen.snapscreen_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    // Base folder for all resumes
    private static final String RESUME_FOLDER = "resumes/";

    @Autowired
    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
        
        // Create the presigner using the same region and credentials as the S3Client
        this.s3Presigner = S3Presigner.builder()
                .region(this.s3Client.serviceClientConfiguration().region())
                .credentialsProvider(this.s3Client.serviceClientConfiguration().credentialsProvider())
                .build();
    }
    
    /**
     * Upload a resume file to S3, replacing any existing resume for the user
     * @param userId The user ID (from Firebase)
     * @param file The resume file to upload
     * @return The S3 object key of the uploaded file
     * @throws IOException if there's an error reading the file
     */
    public String uploadResume(String userId, MultipartFile file) throws IOException {
        // First, delete any existing resume for this user
        deleteExistingResume(userId);
        
        // Generate a unique file name based on original file name to avoid collisions
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String objectKey = RESUME_FOLDER + userId + "/resume" + fileExtension;
        
        // Set content type based on file extension
        String contentType = determineContentType(fileExtension);
        
        // Upload the file to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .metadata(Map.of(
                    "userId", userId,
                    "originalFileName", file.getOriginalFilename()
                ))
                .build();
        
        s3Client.putObject(putObjectRequest, 
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        
        return objectKey;
    }
    
    /**
     * Delete any existing resume for a user
     * @param userId The user ID
     */
    public void deleteExistingResume(String userId) {
        String prefix = RESUME_FOLDER + userId + "/";
        
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        
        ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsRequest);
        
        // Delete each object found with this user's prefix
        response.contents().forEach(s3Object -> {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Object.key())
                    .build();
            
            s3Client.deleteObject(deleteRequest);
        });
    }
    
    /**
     * Get the resume for a user (if it exists)
     * @param userId The user ID
     * @return Optional with the object key if the resume exists, empty otherwise
     */
    public Optional<String> getUserResume(String userId) {
        String prefix = RESUME_FOLDER + userId + "/";
        
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        
        ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsRequest);
        
        // Each user should only have one resume, so we just take the first one if exists
        if (response.contents().isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(response.contents().get(0).key());
        }
    }
    
    /**
     * Get a pre-signed URL for a resume file
     * @param objectKey The S3 object key of the file
     * @param expirationInMinutes How long the URL should be valid for (in minutes)
     * @return The pre-signed URL
     */
    public String getPreSignedUrl(String objectKey, long expirationInMinutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationInMinutes))
                .getObjectRequest(getObjectRequest)
                .build();
        
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        
        return presignedRequest.url().toString();
    }
    
    /**
     * Delete a resume file from S3
     * @param objectKey The S3 object key of the file to delete
     */
    public void deleteFile(String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        
        s3Client.deleteObject(deleteObjectRequest);
    }
    
    /**
     * Check if a user has a resume
     * @param userId The user ID
     * @return true if the user has a resume, false otherwise
     */
    public boolean hasResume(String userId) {
        return getUserResume(userId).isPresent();
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
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * Determine the content type based on file extension
     * @param fileExtension The file extension
     * @return The MIME type for the file
     */
    private String determineContentType(String fileExtension) {
        if (fileExtension == null) {
            return "application/octet-stream";
        }
        
        return switch (fileExtension.toLowerCase()) {
            case ".pdf" -> "application/pdf";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }
} 