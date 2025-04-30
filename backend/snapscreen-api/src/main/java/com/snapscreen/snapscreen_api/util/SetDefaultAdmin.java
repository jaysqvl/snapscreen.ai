package com.snapscreen.snapscreen_api.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to set default admin users in development environments
 */
@Configuration
public class SetDefaultAdmin {

    /**
     * Create a CommandLineRunner bean that will run on application startup
     * only when the "dev" profile is active
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner setDefaultAdminUser() {
        return args -> {
            // List of admin user IDs
            List<String> defaultAdminUids = Arrays.asList(
                "zmqHybv5WtdfrrC4swWDD8QZHuh2", // Test User
                "2YtqhaX6fxcKV9lvWWiIW5A0P2p2" // Google Account
            );
            
            System.out.println("Setting default admin users: " + defaultAdminUids);
            
            try {
                // Create admin claims
                Map<String, Object> claims = new HashMap<>();
                claims.put("admin", true);
                
                // Set custom claims on each user
                for (String uid : defaultAdminUids) {
                    FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
                    System.out.println("Admin privileges set for user: " + uid);
                }
                
                System.out.println("Default admin users set successfully!");
            } catch (FirebaseAuthException e) {
                System.err.println("Error setting default admin: " + e.getMessage());
            }
        };
    }
} 