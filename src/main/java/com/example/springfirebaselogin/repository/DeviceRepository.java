package com.example.springfirebaselogin.repository;

import com.example.springfirebaselogin.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByDeviceName(String deviceName);
    List<Device> findByProfileIdAndIsActive(Integer profileId, Integer isActive);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE device SET is_active=0 " +
            "WHERE device_id=:deviceId and profile_id=:profileId", nativeQuery = true)
    void deactivateDevice(Integer deviceId, Integer profileId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE device SET is_active=1 " +
            "WHERE device_id=:deviceId and profile_id=:profileId", nativeQuery = true)
    void activateDevice(Integer deviceId, Integer profileId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE device SET token=:token " +
            "WHERE device_id=:deviceId and profile_id=:profileId", nativeQuery = true)
    void registerToken(Integer deviceId, Integer profileId, String token);

}
