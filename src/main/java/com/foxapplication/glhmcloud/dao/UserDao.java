package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDTO extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);
    boolean existsByUsername(String username);
    List<UserEntity> findAllByOrganizationID(UUID organizationID);
}
