package com.snapscreen.snapscreen_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    /**
     * Health check endpoint for the auth service
     * @return Simple status message
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "active", 
            "message", "Firebase authentication is being handled client-side. " +
                      "Use ID tokens from Firebase in the Authorization header for authenticated requests."
        );
    }
} 