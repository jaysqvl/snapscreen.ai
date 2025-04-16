package com.snapscreen.snapscreen_api.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the UserController.
 * These tests connect to Firebase Auth to verify actual integration.
 * 
 * Note: For these tests to work, you need a valid Firebase user.
 * You may need to create a test user in Firebase before running.
 * 
 * To run these tests specifically:
 * ./mvnw test -Dgroups=integration
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Tag("integration")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Replace with a real user ID from your Firebase project
    private static final String TEST_USER_ID = "integration_test_user";

    /**
     * Tests retrieving a user from Firebase
     * Note: This requires a real user to exist in Firebase
     */
    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{uid}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").exists());
    }

    /**
     * Tests listing users from Firebase
     */
    @Test
    public void testListUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("maxResults", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests setting admin role on a Firebase user
     * Note: This modifies a real user's claims in Firebase
     */
    @Test
    public void testSetAdminRole() throws Exception {
        // First make the user an admin
        mockMvc.perform(put("/api/users/{uid}/admin", TEST_USER_ID)
                .param("isAdmin", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then remove admin role (to clean up)
        mockMvc.perform(put("/api/users/{uid}/admin", TEST_USER_ID)
                .param("isAdmin", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Tests setting custom claims on a Firebase user
     * Note: This modifies a real user's claims in Firebase
     */
    @Test
    public void testSetCustomClaims() throws Exception {
        // Set custom claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "integration_tester");
        claims.put("testTimestamp", System.currentTimeMillis());

        mockMvc.perform(put("/api/users/{uid}/claims", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"integration_tester\",\"testTimestamp\":" + System.currentTimeMillis() + "}"))
                .andExpect(status().isOk());
        
        // You could add verification by retrieving the user and checking claims
        // if Firebase Admin SDK exposes that functionality in your endpoint
    }
} 