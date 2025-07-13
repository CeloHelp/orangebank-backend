package com.orangejuice.orangebank_backend.repository;

import com.orangejuice.orangebank_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCpf(String cpf);
    
    Optional<User> findByEmailOrCpf(String email, String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
} 