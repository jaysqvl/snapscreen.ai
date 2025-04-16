package com.snapscreen.snapscreen_api.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
                                    throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                // Verify the ID token with Firebase
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                
                // Extract claims and set roles
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                
                // Add basic role
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                
                // Add admin role if present in custom claims
                Map<String, Object> claims = decodedToken.getClaims();
                if (claims != null && Boolean.TRUE.equals(claims.get("admin"))) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                
                // Create authentication token
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    uid,           // Principal (user ID)
                    null,          // Credentials (not needed)
                    authorities    // Authorities/roles
                );
                
                // Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(auth);
                
            } catch (FirebaseAuthException e) {
                // Token is invalid, continue without setting authentication
                logger.warn("Invalid Firebase ID token: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 