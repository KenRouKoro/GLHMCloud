package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.devicerecord.BaseDeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceSecRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceSecRecordDao extends JpaRepository<DeviceSecRecordEntity, Long> {
    // 直接删除时间戳早于指定时间的数据（需支持批量删除）
    @Modifying
    @Query("DELETE FROM DeviceSecRecordEntity e WHERE e.time < :date")
    void deleteByCreatedTimeBefore(@Param("date") Long date);

    @Query("SELECT e FROM DeviceSecRecordEntity e WHERE e.device_id = :deviceId AND e.time BETWEEN :startTime AND :endTime")
    List<DeviceSecRecordEntity> findByIdBetween(@Param("startTime")Long startTime, @Param("endTime") Long endTime, @Param("deviceId") String deviceId);

    @Query("SELECT EXISTS (SELECT 1 FROM DeviceSecRecordEntity e WHERE e.device_id = :deviceId AND e.time BETWEEN :startTime AND :endTime)")
    boolean existsByDevice_idAndTime(@Param("deviceId") String deviceId,@Param("startTime")Long startTime, @Param("endTime") Long endTime);

    @Query("SELECT e FROM DeviceSecRecordEntity e WHERE e.device_id = :deviceId ORDER BY e.time DESC")
    Page<DeviceSecRecordEntity> findAllByOrderByTimeDesc(Pageable pageable,@Param("deviceId") String deviceId);

    @Query("SELECT EXISTS (SELECT 1 FROM DeviceSecRecordEntity e WHERE e.device_id = :deviceId)")
    boolean existsByDevice_id(String deviceId);
    @Query("SELECT e FROM DeviceSecRecordEntity e WHERE e.device_id = :deviceId")
    long countByDevice_id(String deviceId);
}
