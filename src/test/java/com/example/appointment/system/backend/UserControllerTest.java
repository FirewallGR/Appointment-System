package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.UserController;
import com.example.appointment.system.backend.dto.JwtRequestDTO;
import com.example.appointment.system.backend.dto.JwtResponceDTO;
import com.example.appointment.system.backend.dto.RegistrationUserDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.service.UserService;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.utils.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private final UserService userService = Mockito.mock(UserService.class);
    private final JwtTokenUtils jwtTokenUtils = Mockito.mock(JwtTokenUtils.class);
    private final AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final UserController userController = new UserController(userService, jwtTokenUtils, authenticationManager, userMapper);

    @Test
    void testCreateAuthToken_Success() {
        // Arrange
        JwtRequestDTO authRequest = new JwtRequestDTO("testUser", "testPassword");
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        String mockToken = "mockToken";

        when(userService.loadUserByUsername(authRequest.getUsername())).thenReturn(mockUserDetails);
        when(jwtTokenUtils.generateToken(mockUserDetails)).thenReturn(mockToken);

        // Act
        ResponseEntity<?> response = userController.createAuthToken(authRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new JwtResponceDTO(mockToken), response.getBody());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testCreateAuthToken_BadCredentials() {
        // Arrange
        JwtRequestDTO authRequest = new JwtRequestDTO("testUser", "wrongPassword");
        doThrow(BadCredentialsException.class).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act
        ResponseEntity<?> response = userController.createAuthToken(authRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        AppErorr error = (AppErorr) response.getBody();
        assertEquals("Неправильный логин или пароль", error.getMessage());
    }

    @Test
    void testRegistration_Success() {
        // Arrange
        RegistrationUserDTO regUser = new RegistrationUserDTO("testUser", "password", "password", "John", "Doe", "2");
        User newUser = new User();
        when(userService.findByUsername(regUser.getUsername())).thenReturn(Optional.empty());
        when(userMapper.RegDtoToUser(regUser)).thenReturn(newUser);

        // Act
        ResponseEntity<?> response = userController.registration(regUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Успешная регистрация", response.getBody());
        verify(userService).createNewUser(newUser);
    }

    @Test
    void testRegistration_MissingFields() {
        // Arrange
        RegistrationUserDTO regUser = new RegistrationUserDTO(null, "password", "password", "John", "Doe", "3");

        // Act
        ResponseEntity<?> response = userController.registration(regUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        AppErorr error = (AppErorr) response.getBody();
        assertEquals("Все обязательные поля должны быть заполнены!", error.getMessage());
    }

    @Test
    void testRegistration_PasswordMismatch() {
        // Arrange
        RegistrationUserDTO regUser = new RegistrationUserDTO("testUser", "password1", "password2", "John", "Doe", "4");

        // Act
        ResponseEntity<?> response = userController.registration(regUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        AppErorr error = (AppErorr) response.getBody();
        assertEquals("Пароли не совпадают", error.getMessage());
    }

    @Test
    void testRegistration_UsernameTaken() {
        // Arrange
        RegistrationUserDTO regUser = new RegistrationUserDTO("testUser", "password", "password", "John", "Doe", "5");
        User existingUser = new User();
        when(userService.findByUsername(regUser.getUsername())).thenReturn(Optional.of(existingUser));

        // Act
        ResponseEntity<?> response = userController.registration(regUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        AppErorr error = (AppErorr) response.getBody();
        assertEquals("Имя пользователя занято", error.getMessage());
    }
}
