package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceDTO extends JpaRepository<DeviceEntity, String> {
}
