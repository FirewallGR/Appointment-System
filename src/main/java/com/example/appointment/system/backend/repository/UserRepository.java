package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    List<User> findByRolesName(String roleName);
    void deleteByUsername(String username);
}
