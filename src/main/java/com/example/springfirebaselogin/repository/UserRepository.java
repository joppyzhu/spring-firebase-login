package com.example.springfirebaselogin.repository;

import com.example.springfirebaselogin.model.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByUsername(String username);
    Optional<Profile> findByProfileId(Integer profileId);
    Optional<Profile> findByEmail(String email);
}
