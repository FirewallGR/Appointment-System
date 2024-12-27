package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.dto.security.JwtRequestDTO;
import com.example.appointment.system.backend.dto.security.JwtResponceDTO;
import com.example.appointment.system.backend.dto.security.RegistrationUserDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.service.UserService;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new AppErorr(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponceDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO regUser) {
        if (!userService.isRegistrationValid(regUser)) {
            return new ResponseEntity<>(
                    new AppErorr(HttpStatus.BAD_REQUEST.value(), "Неправильные данные для регистрации"),
                    HttpStatus.BAD_REQUEST
            );
        }
        userService.registerUser(regUser);
        return ResponseEntity.ok("{\"result\" : \"Успешная регистрация\"}");
    }

}
