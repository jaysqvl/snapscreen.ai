package com.snapscreen.snapscreen_api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

/**
 * Test security configuration that overrides the main security config
 * in test environments. This bypasses Firebase authentication for tests.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        // For tests, disable Firebase auth and use in-memory authentication
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Allow all requests in tests
                )
                .build();
    }

    /**
     * Creates an in-memory user with admin role for testing
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.builder()
                .username("zmqHybv5WtdfrrC4swWDD8QZHuh2")
                .password("{noop}password")
                .authorities(Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                ))
                .build();

        UserDetails regularUser = User.builder()
                .username("integration_test_user")
                .password("{noop}password")
                .authorities(Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER")
                ))
                .build();

        return new InMemoryUserDetailsManager(Arrays.asList(adminUser, regularUser));
    }
} 