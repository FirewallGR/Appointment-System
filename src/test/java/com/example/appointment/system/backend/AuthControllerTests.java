package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.AuthController;
import com.example.appointment.system.backend.dto.security.JwtRequestDTO;
import com.example.appointment.system.backend.dto.security.JwtResponceDTO;
import com.example.appointment.system.backend.dto.security.RegistrationUserDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.service.UserService;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.utils.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTests {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAuthToken_Success() {
        JwtRequestDTO authRequest = new JwtRequestDTO("username", "password");
        when(userService.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn("jwtToken");

        ResponseEntity<?> response = authController.createAuthToken(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponceDTO);
        JwtResponceDTO responseDTO = (JwtResponceDTO) response.getBody();
        assertEquals("jwtToken", responseDTO.getToken());
    }

    @Test
    void testCreateAuthToken_BadCredentials() {
        JwtRequestDTO authRequest = new JwtRequestDTO("username", "password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.createAuthToken(authRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppErorr);
        AppErorr error = (AppErorr) response.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        assertEquals("Неправильный логин или пароль", error.getMessage());
    }

    @Test
    void testRegistration_Success() {
        RegistrationUserDTO regUser = new RegistrationUserDTO("username", "email","password", "password", "name", "name2", "name3");
        when(userService.isRegistrationValid(regUser)).thenReturn(true);

        ResponseEntity<?> response = authController.registration(regUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"result\" : \"Успешная регистрация\"}", response.getBody());
    }

    @Test
    void testRegistration_BadRequest() {
        RegistrationUserDTO regUser = new RegistrationUserDTO("username", "email","password", "password", "name", "name2", "name3");
        when(userService.isRegistrationValid(regUser)).thenReturn(false);

        ResponseEntity<?> response = authController.registration(regUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppErorr);
        AppErorr error = (AppErorr) response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Неправильные данные для регистрации", error.getMessage());
    }
}
