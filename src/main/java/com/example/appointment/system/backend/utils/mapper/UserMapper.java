package com.example.appointment.system.backend.utils.mapper;

import com.example.appointment.system.backend.dto.security.RegistrationUserDTO;
import com.example.appointment.system.backend.dto.user.UserRequestDTO;
import com.example.appointment.system.backend.dto.user.UserResponseDTO;
import com.example.appointment.system.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User RegDtoToUser(RegistrationUserDTO registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(registrationUserDto.getPassword()); // Пароль не шифруется на этом уровне
        user.setEmail(registrationUserDto.getEmail());
        user.setName(registrationUserDto.getName());
        user.setSecondName(registrationUserDto.getSecondName());
        user.setThirdName(registrationUserDto.getThirdName());
        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSecondName(),
                user.getThirdName(),
                user.getEmail()
        );
    }

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Без шифрования
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSecondName(dto.getSecondName());
        user.setThirdName(dto.getThirdName());
        return user;
    }

    public void updateUserFromDTO(UserRequestDTO dto, User user) {
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Без шифрования
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSecondName(dto.getSecondName());
        user.setThirdName(dto.getThirdName());
    }
}
