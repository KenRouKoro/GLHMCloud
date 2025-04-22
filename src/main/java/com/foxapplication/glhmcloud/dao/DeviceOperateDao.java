package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.devicerecord.BaseDeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceOperateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceOperateDao extends JpaRepository<DeviceOperateEntity, Long> {
}
