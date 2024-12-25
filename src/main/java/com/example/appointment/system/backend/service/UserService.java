package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.dto.DoctorDTO;
import com.example.appointment.system.backend.dto.UserDTO;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.repository.RoleRepository;
import com.example.appointment.system.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        User user = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                                String.format("Пользователь с именем '%s' не найден", username)
                        )
                );
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(
                        role -> new SimpleGrantedAuthority(role.getName())
                ).collect(Collectors.toList())
        );
    }

    public void createNewUser(User user, String role){
        if(role.equals("client")) {
            user.setRoles(List.of(roleRepository.findByName("ROLE_PATIENT").get()));
        } else if (role.equals("doctor")) {
            user.setRoles(List.of(roleRepository.findByName("ROLE_DOCTOR").get()));
        } else if (role.equals("manager")) {
            user.setRoles(List.of(roleRepository.findByName("ROLE_MANAGER").get()));
        } else {
            return;
        }
        userRepository.save(user);
    }
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public List<DoctorDTO> getAllDoctors() {
        List<User> doctors = userRepository.findByRolesName("ROLE_DOCTOR");
        return doctors.stream().map(this::mapToDoctorDTO).collect(Collectors.toList());
    }
    public List<UserDTO> getAllUsers() {
        List<User> users =  userRepository.findByRolesName("ROLE_PATIENT");
        return users.stream().map(this::mapToUserDTO).collect(Collectors.toList());
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
    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getName());
        userDTO.setLastName(user.getSecondName());
        userDTO.setMiddleName(user.getThirdName());
        return userDTO;
    }

}