package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.UserController;
import com.example.appointment.system.backend.dto.user.UserRequestDTO;
import com.example.appointment.system.backend.dto.user.UserResponseDTO;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.service.UserService;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTests {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<UserResponseDTO> mockUsers = List.of(
                new UserResponseDTO(UUID.randomUUID(), "user1", "User", "One", "Two", "Email1"),
                new UserResponseDTO(UUID.randomUUID(), "user2", "User", "One", "Two", "Email2")
        );

        when(userService.getAllUsers()).thenReturn(mockUsers);

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUsers, response.getBody());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UUID userId = UUID.randomUUID();
        UserResponseDTO mockUser = new UserResponseDTO(userId, "user1", "User", "One", "Two", "Email1");

        when(userService.getUserById(userId)).thenReturn(mockUser);

        ResponseEntity<UserResponseDTO> response = userController.getUserById(userId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
    }


    @Test
    void getUserInfo_ShouldReturnUnauthorizedWhenTokenInvalid() {
        String token = "Bearer invalidToken";

        when(jwtTokenUtils.getUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<?> response = userController.getUserInfo(token);

        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "user1", "User", "One", "Two", "Email1");

        when(userService.createUser(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.createUser(requestDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        UUID userId = UUID.randomUUID();
        UserRequestDTO requestDTO = new UserRequestDTO();
        UserResponseDTO responseDTO = new UserResponseDTO(userId, "user1", "User", "One", "Two", "Email1");

        when(userService.updateUser(userId, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(userId, requestDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        UUID userId = UUID.randomUUID();

        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
    }
}