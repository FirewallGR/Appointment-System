package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.dto.user.UserRequestDTO;
import com.example.appointment.system.backend.dto.user.*;
import com.example.appointment.system.backend.dto.user.UserResponseDTO;
import com.example.appointment.system.backend.dto.security.RegistrationUserDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.model.Role;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.service.UserService;
import com.example.appointment.system.backend.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
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
        List<UserResponseDTO> patients = userService.getAllUsers();
        return ResponseEntity.ok(patients);
    }


    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.createUser(userRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
