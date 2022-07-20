package com.example.springfirebaselogin.repository;

import com.example.springfirebaselogin.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByDeviceName(String deviceName);

}
