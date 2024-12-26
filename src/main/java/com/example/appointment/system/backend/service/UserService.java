package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.dto.user.DoctorDTO;
import com.example.appointment.system.backend.dto.user.UserDTO;
import com.example.appointment.system.backend.dto.user.UserRequestDTO;
import com.example.appointment.system.backend.dto.user.UserResponseDTO;
import com.example.appointment.system.backend.dto.security.RegistrationUserDTO;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.repository.RoleRepository;
import com.example.appointment.system.backend.repository.UserRepository;
import com.example.appointment.system.backend.utils.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<UserResponseDTO> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифрование здесь
        user.setRoles(List.of(roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Role not found"))));
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(UUID id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromDTO(dto, user);
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO registerUser(RegistrationUserDTO registrationUserDTO) {
        User user = userMapper.RegDtoToUser(registrationUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифрование здесь
        user.setRoles(List.of(roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Role not found"))));
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь с именем '%s' не найден", username)
                ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    public boolean isRegistrationValid(RegistrationUserDTO regUser) {
        if (regUser.getUsername() == null || regUser.getPassword() == null ||
                regUser.getName() == null || regUser.getSecondName() == null ||
                regUser.getConfirmPassword() == null) {
            return false;
        }
        if (!regUser.getPassword().equals(regUser.getConfirmPassword())) {
            return false;
        }
        return userRepository.findByUsername(regUser.getUsername()).isEmpty();
    }

    public List<DoctorDTO> getAllDoctors() {
        List<User> doctors = userRepository.findByRolesName("ROLE_DOCTOR");
        return doctors.stream().map(this::mapToDoctorDTO).collect(Collectors.toList());
    }

    private DoctorDTO mapToDoctorDTO(User user) {
        return new DoctorDTO(
                user.getId(),
                user.getName(),
                user.getSecondName(),
                user.getThirdName(),
                user.getUsername()
        );
    }
}
