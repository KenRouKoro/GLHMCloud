package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeviceDao extends JpaRepository<DeviceEntity, String> {
    // 新增查询所有在线设备的方法
    List<DeviceEntity> findAllByOnline(boolean online);
    @Query("SELECT e FROM DeviceEntity e WHERE e.organization_id = :organizationID")
    List<DeviceEntity> findAllByOrganization_id(@Param("organizationID") UUID organizationID);
    @Modifying
    @Query("DELETE FROM DeviceEntity e WHERE e.organization_id = :organizationID")
    void deleteAllByOrganization_id(@Param("organizationID")UUID organizationID);
}