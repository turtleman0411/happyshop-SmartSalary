package com.example.demo.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.model.User;
import com.example.demo.domain.value.Account;
import com.example.demo.domain.value.UserId;

public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByAccount(Account account);
    boolean existsByAccount_Value(String value);
}
    


