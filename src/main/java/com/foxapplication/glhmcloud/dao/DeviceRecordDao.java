package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.devicerecord.BaseDeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceRecordDao extends JpaRepository<DeviceRecordEntity, Long> {
    @Query("SELECT e FROM DeviceRecordEntity e WHERE e.device_id = :deviceId AND e.time BETWEEN :startTime AND :endTime")
    List<DeviceRecordEntity> findByIdBetween(@Param("startTime")Long startTime, @Param("endTime") Long endTime, @Param("deviceId") String deviceId);

    @Query("SELECT e FROM DeviceRecordEntity e WHERE e.device_id = :deviceId ORDER BY e.time DESC")
    Page<DeviceRecordEntity> findAllByOrderByTimeDesc(Pageable pageable,@Param("deviceId") String deviceId);

    @Query("SELECT EXISTS (SELECT 1 FROM DeviceRecordEntity e WHERE e.device_id = :deviceId)")
    boolean existsByDevice_id(String deviceId);
    @Query("SELECT e FROM DeviceRecordEntity e WHERE e.device_id = :deviceId")
    long countByDevice_id(String deviceId);
}