package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.view.UserViewData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);
    boolean existsByUsername(String username);
    @Query("SELECT e FROM UserEntity e WHERE e.organization_id = :organizationID")
    List<UserEntity> findAllByOrganization_id(@Param("organizationID") UUID organizationID);
    @Query("SELECT new com.foxapplication.glhmcloud.param.view.UserViewData(e) FROM UserEntity e WHERE e.organization_id = :organizationID")
    List<UserViewData> findAllByOrganization_idSafe(@Param("organizationID") UUID organizationID);
    @Query("SELECT new com.foxapplication.glhmcloud.param.view.UserViewData(e)  FROM UserEntity e")
    List<UserViewData> findAllSafe();
}
