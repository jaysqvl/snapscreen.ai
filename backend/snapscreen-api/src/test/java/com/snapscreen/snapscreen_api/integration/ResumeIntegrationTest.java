package com.snapscreen.snapscreen_api.integration;

import com.snapscreen.snapscreen_api.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the ResumeController.
 * These tests connect to real services (S3, PostgreSQL) to verify actual integration.
 * 
 * To run these tests specifically:
 * ./mvnw test -Dgroups=integration
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Tag("integration")
@Import(TestSecurityConfig.class)
public class ResumeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String TEST_USER_ID = "integration_test_user";

    /**
     * Tests the full resume upload and retrieval flow using real S3:
     * 1. Upload a resume
     * 2. Verify it exists
     * 3. Retrieve it
     * 4. Delete it
     */
    @Test
    @WithMockUser(username = "integration_test_user", roles = "USER")
    public void testResumeFullFlow() throws Exception {
        // Create a test PDF file
        MockMultipartFile testResume = new MockMultipartFile(
                "file",
                "integration_test.pdf",
                "application/pdf",
                "Integration test resume content".getBytes()
        );

        // 1. Upload the resume
        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(testResume))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.filename").value("integration_test.pdf"));

        // 2. Check if resume exists
        mockMvc.perform(get("/api/resumes/{userId}/exists", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasResume").value(true));

        // 3. Get the resume data
        mockMvc.perform(get("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.filename").exists());

        // 4. Clean up - delete the test resume
        mockMvc.perform(delete("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Verify it was deleted
        mockMvc.perform(get("/api/resumes/{userId}/exists", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasResume").value(false));
    }

    /**
     * Tests the URL generation for resumes
     */
    @Test
    @WithMockUser(username = "integration_test_user", roles = "USER")
    public void testGetResumeUrl() throws Exception {
        // This assumes there's a sample object in the S3 bucket
        // If needed, you could upload a file first, then test URL generation
        
        String objectKey = "resumes/" + TEST_USER_ID + "/sample.pdf";
        
        mockMvc.perform(get("/api/resumes/url")
                .param("objectKey", objectKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    /**
     * Tests error cases with real services
     */
    @Test
    @WithMockUser(username = "integration_test_user", roles = "USER")
    public void testUploadInvalidFile() throws Exception {
        // Test with empty file
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File is empty"));

        // Test with invalid file type
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "Invalid content".getBytes()
        );

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid file type. Supported types: PDF, DOC, DOCX, TXT"));
    }
} 