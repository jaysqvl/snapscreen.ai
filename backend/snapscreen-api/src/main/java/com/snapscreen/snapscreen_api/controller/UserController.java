package com.snapscreen.snapscreen_api.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.snapscreen.snapscreen_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserRecord> getUserById(@PathVariable String uid) {
        try {
            UserRecord user = userService.getUserById(uid);
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserRecord>> listUsers(@RequestParam(defaultValue = "1000") int maxResults) {
        try {
            List<UserRecord> users = userService.listUsers(maxResults);
            return ResponseEntity.ok(users);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{uid}/admin")
    public ResponseEntity<Void> setAdminRole(@PathVariable String uid, @RequestParam boolean isAdmin) {
        try {
            userService.setAdminRole(uid, isAdmin);
            return ResponseEntity.ok().build();
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{uid}/claims")
    public ResponseEntity<Void> setCustomClaims(@PathVariable String uid, @RequestBody Map<String, Object> claims) {
        try {
            userService.setCustomUserClaims(uid, claims);
            return ResponseEntity.ok().build();
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 