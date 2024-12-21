package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.dto.*;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.utils.mapper.UserMapper;
import com.example.appointment.system.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.example.appointment.system.backend.model.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
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
        System.out.println(jwtTokenUtils.getUsername(token));
        return ResponseEntity.ok(new JwtResponceDTO(token));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtTokenUtils.getUsername(token);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            // Собираем роли пользователя
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getName());
            userInfo.put("secondName", user.getSecondName());
            userInfo.put("thirdName", user.getThirdName());
            userInfo.put("roles", roles);

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.UNAUTHORIZED.value(), "Неверный токен"), HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        List<DoctorDTO> doctors = userService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllPatients() {
        List<UserDTO> patients = userService.getAllUsers();
        return ResponseEntity.ok(patients);
    }


    @PostMapping("/reg")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDTO regUser) {
        if (regUser.getUsername() == "" || regUser.getPassword() == "" ||
                regUser.getName() == "" || regUser.getSecondName() == "" ||
                regUser.getConfirmPassword() == "") {
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
        userService.createNewUser(newUser, regUser.getRole());
        return ResponseEntity.ok("Успешная регистрация");
    }
}
