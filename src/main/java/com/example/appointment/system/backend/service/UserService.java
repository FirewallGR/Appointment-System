package com.example.appointment.system.backend.service;

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

    public void createNewUser(User user){
        user.setRoles(List.of(roleRepository.findByName("ROLE_PATIENT").get()));
        userRepository.save(user);
    }
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }
}