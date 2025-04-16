package com.snapscreen.snapscreen_api.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.snapscreen.snapscreen_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        UserRecord mockUser = mock(UserRecord.class);
        when(userService.getUserById(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/testUserId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        when(userService.getUserById(anyString())).thenThrow(exception);

        mockMvc.perform(get("/api/users/nonExistentUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListUsers_Success() throws Exception {
        UserRecord mockUser1 = mock(UserRecord.class);
        UserRecord mockUser2 = mock(UserRecord.class);
        List<UserRecord> mockUsers = Arrays.asList(mockUser1, mockUser2);
        when(userService.listUsers(anyInt())).thenReturn(mockUsers);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testListUsers_Error() throws Exception {
        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        when(userService.listUsers(anyInt())).thenThrow(exception);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSetAdminRole_Success() throws Exception {
        mockMvc.perform(put("/api/users/testUserId/admin")
                .param("isAdmin", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetAdminRole_Error() throws Exception {
        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        doThrow(exception).when(userService).setAdminRole(anyString(), eq(true));

        mockMvc.perform(put("/api/users/testUserId/admin")
                .param("isAdmin", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSetCustomClaims_Success() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "editor");

        mockMvc.perform(put("/api/users/testUserId/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"editor\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetCustomClaims_Error() throws Exception {
        FirebaseAuthException exception = mock(FirebaseAuthException.class);
        doThrow(exception).when(userService).setCustomUserClaims(anyString(), anyMap());

        mockMvc.perform(put("/api/users/testUserId/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"editor\"}"))
                .andExpect(status().isBadRequest());
    }
} 