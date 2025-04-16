package com.snapscreen.snapscreen_api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.ListUsersPage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    /**
     * Get a user by their Firebase UID
     * @param uid Firebase user ID
     * @return UserRecord object containing user information
     * @throws FirebaseAuthException if the user doesn't exist or there's an authentication error
     */
    public UserRecord getUserById(String uid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUser(uid);
    }
    
    /**
     * List all users in Firebase (paged)
     * @param maxResults maximum number of users to return
     * @return List of UserRecord objects
     * @throws FirebaseAuthException if there's an authentication error
     */
    public List<UserRecord> listUsers(int maxResults) throws FirebaseAuthException {
        // Get the page of users
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null, maxResults);
        
        List<UserRecord> users = new ArrayList<>();
        // Iterate through the results and add to our list
        for (UserRecord user : page.iterateAll()) {
            users.add(user);
        }
        
        return users;
    }
    
    /**
     * Create a custom claim for a user
     * @param uid Firebase user ID
     * @param claims Map of custom claims to set
     * @throws FirebaseAuthException if the user doesn't exist or there's an authentication error
     */
    public void setCustomUserClaims(String uid, Map<String, Object> claims) throws FirebaseAuthException {
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
    }
    
    /**
     * Set admin role for a user
     * @param uid Firebase user ID
     * @param isAdmin whether the user should be an admin
     * @throws FirebaseAuthException if the user doesn't exist or there's an authentication error
     */
    public void setAdminRole(String uid, boolean isAdmin) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", isAdmin);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
    }
} 