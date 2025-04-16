package com.snapscreen.snapscreen_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.sdk.path:classpath:firebase-service-account.json}")
    private String firebaseSdkPath;
    
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // For development with local file
            InputStream serviceAccount;
            
            try {
                // Try to load from classpath resource
                if (firebaseSdkPath.startsWith("classpath:")) {
                    String resourcePath = firebaseSdkPath.substring("classpath:".length());
                    serviceAccount = new ClassPathResource(resourcePath).getInputStream();
                } else {
                    // It's a file path
                    serviceAccount = new FileInputStream(firebaseSdkPath);
                }
            } catch (IOException e) {
                throw new IOException("Firebase service account file not found at: " + firebaseSdkPath, e);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }
} 