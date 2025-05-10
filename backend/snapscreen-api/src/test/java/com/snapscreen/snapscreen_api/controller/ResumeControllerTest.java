package com.snapscreen.snapscreen_api.controller;

import com.snapscreen.snapscreen_api.config.TestSecurityConfig;
import com.snapscreen.snapscreen_api.service.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private TestResumeController testResumeController;

    private final String TEST_USER_ID = "testUserId";
    private final String TEST_OBJECT_KEY = "resumes/testUserId/resume.pdf";
    private final String TEST_PRESIGNED_URL = "https://example-bucket.s3.amazonaws.com/resumes/testUserId/resume.pdf";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(testResumeController).build();
        
        // Default behavior for mocks
        when(s3StorageService.getPreSignedUrl(anyString(), anyInt())).thenReturn(TEST_PRESIGNED_URL);
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testUploadResume_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "test resume content".getBytes()
        );

        when(s3StorageService.uploadResume(eq(TEST_USER_ID), any())).thenReturn(TEST_OBJECT_KEY);

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").value(TEST_OBJECT_KEY))
                .andExpect(jsonPath("$.url").value(TEST_PRESIGNED_URL))
                .andExpect(jsonPath("$.filename").value("resume.pdf"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testUploadResume_NoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "test resume content".getBytes()
        );

        when(s3StorageService.uploadResume(eq(TEST_USER_ID), any())).thenReturn(TEST_OBJECT_KEY);
        when(s3StorageService.getPreSignedUrl(eq(TEST_OBJECT_KEY), anyInt())).thenReturn(null);

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").value(TEST_OBJECT_KEY))
                .andExpect(jsonPath("$.url").doesNotExist())
                .andExpect(jsonPath("$.filename").value("resume.pdf"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testUploadResume_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File is empty"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testUploadResume_InvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.exe",
                "application/octet-stream",
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/resumes/{userId}/upload", TEST_USER_ID)
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid file type. Supported types: PDF, DOC, DOCX, TXT"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testGetUserResume_Success() throws Exception {
        when(s3StorageService.getUserResume(TEST_USER_ID)).thenReturn(Optional.of(TEST_OBJECT_KEY));

        mockMvc.perform(get("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").value(TEST_OBJECT_KEY))
                .andExpect(jsonPath("$.url").value(TEST_PRESIGNED_URL))
                .andExpect(jsonPath("$.filename").value("resume.pdf"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testGetUserResume_NoUrl() throws Exception {
        when(s3StorageService.getUserResume(TEST_USER_ID)).thenReturn(Optional.of(TEST_OBJECT_KEY));
        when(s3StorageService.getPreSignedUrl(eq(TEST_OBJECT_KEY), anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").value(TEST_OBJECT_KEY))
                .andExpect(jsonPath("$.url").doesNotExist())
                .andExpect(jsonPath("$.filename").value("resume.pdf"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testGetUserResume_NotFound() throws Exception {
        when(s3StorageService.getUserResume(TEST_USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testHasResume_True() throws Exception {
        when(s3StorageService.hasResume(TEST_USER_ID)).thenReturn(true);

        mockMvc.perform(get("/api/resumes/{userId}/exists", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasResume").value(true));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testHasResume_False() throws Exception {
        when(s3StorageService.hasResume(TEST_USER_ID)).thenReturn(false);

        mockMvc.perform(get("/api/resumes/{userId}/exists", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasResume").value(false));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testGetResumeUrl_Success() throws Exception {
        mockMvc.perform(get("/api/resumes/url")
                .param("objectKey", TEST_OBJECT_KEY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(TEST_PRESIGNED_URL));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testGetResumeUrl_Null() throws Exception {
        when(s3StorageService.getPreSignedUrl(eq(TEST_OBJECT_KEY), anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/resumes/url")
                .param("objectKey", TEST_OBJECT_KEY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to generate pre-signed URL"));
    }

    @Test
    @WithMockUser(username = "testUserId", roles = "USER")
    public void testDeleteUserResume() throws Exception {
        doNothing().when(s3StorageService).deleteExistingResume(TEST_USER_ID);

        mockMvc.perform(delete("/api/resumes/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(s3StorageService, times(1)).deleteExistingResume(TEST_USER_ID);
    }
} 