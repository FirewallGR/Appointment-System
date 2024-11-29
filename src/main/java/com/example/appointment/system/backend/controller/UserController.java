package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.dto.JwtResponceDTO;
import com.example.appointment.system.backend.dto.JwtRequestDTO;
import com.example.appointment.system.backend.dto.RegistrationUserDTO;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.utils.UserMapper;
import com.example.appointment.system.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper mapper;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO authRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppErorr(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponceDTO(token));
    }
    @PostMapping("/reg")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO regUser) {
        if (regUser.getUsername() == null || regUser.getPassword() == null ||
                regUser.getName() == null || regUser.getSecondName() == null ||
                regUser.getConfirmPassword() == null) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Все обязательные поля должны быть заполнены!"), HttpStatus.BAD_REQUEST);
        }

        if (!regUser.getPassword().equals(regUser.getConfirmPassword())) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userService.findByUsername(regUser.getUsername());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Имя пользователя занято"), HttpStatus.BAD_REQUEST);
        }

        User newUser = mapper.RegDtoToUser(regUser);
        userService.createNewUser(newUser);
        return ResponseEntity.ok("Успешная регистрация");
    }
}
