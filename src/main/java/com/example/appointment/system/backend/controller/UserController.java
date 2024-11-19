package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.utils.JwtTokenUtils;
import com.example.appointment.system.backend.dto.JwtResponce;
import com.example.appointment.system.backend.dto.JwtRequest;
import com.example.appointment.system.backend.dto.RegistrationUserDto;
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

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper mapper;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppErorr(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponce(token));
    }
    @PostMapping("/reg")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDto regUser) {
        // Проверка всех полей
        if (regUser.getUsername() == null || regUser.getPassword() == null || regUser.getConfirmPassword() == null) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Все поля должны быть заполнены!"), HttpStatus.BAD_REQUEST);
        }

        if (!regUser.getPassword().equals(regUser.getConfirmPassword())) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        } else if (regUser.getPassword() == null || regUser.getPassword().isEmpty()) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Пароль не должен быть пустым"), HttpStatus.BAD_REQUEST);
        }else{
            Optional<User> user = userService.findByUsername(regUser.getUsername());

            if(user.isPresent()){
                return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Имя пользователя занято"), HttpStatus.BAD_REQUEST);
            }
        }

        // using mapper
        User registrationUser = mapper.RegDtoToUser(regUser);

        userService.createNewUser(registrationUser);
        return ResponseEntity.ok("Успешная регистрация");
    }
}
