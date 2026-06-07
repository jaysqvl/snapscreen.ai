package com.snapscreen.snapscreen_api.config;

import com.snapscreen.snapscreen_api.security.FirebaseAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (firebaseEnabled) {
            http.addFilterBefore(new FirebaseAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        return http
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                // Admin-only endpoints
                .requestMatchers("/api/users").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/users/*/admin").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/users/*/claims").hasAuthority("ROLE_ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .build();
    }
}
