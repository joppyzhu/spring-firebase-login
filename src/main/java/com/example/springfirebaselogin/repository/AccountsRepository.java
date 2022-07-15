package com.example.springfirebaselogin.repository;

import com.example.springfirebaselogin.model.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, String> {
    List<Accounts> findByUsernameContainingIgnoreCase(String username);
    Optional<Accounts> findByUserId(Integer userId);
}
