package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.devicerecord.BaseDeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceOperateEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceOperateDao extends JpaRepository<DeviceOperateEntity, Long> {
    @Query("SELECT e FROM DeviceOperateEntity e WHERE e.device_id = :deviceId AND e.time BETWEEN :startTime AND :endTime")
    List<DeviceOperateEntity> findByIdBetween(@Param("startTime")Long startTime, @Param("endTime") Long endTime, @Param("deviceId") String deviceId);


    @Query("SELECT e FROM DeviceOperateEntity e WHERE e.device_id = :deviceId ORDER BY e.time DESC")
    Page<DeviceOperateEntity> findAllByOrderByTimeDesc(Pageable pageable,@Param("deviceId") String deviceId);
    @Query("SELECT EXISTS (SELECT 1 FROM DeviceOperateEntity e WHERE e.device_id = :deviceId)")
    boolean existsByDevice_id(String deviceId);
    @Query("SELECT e FROM DeviceOperateEntity e WHERE e.device_id = :deviceId")
    long countByDevice_id(String deviceId);
}
