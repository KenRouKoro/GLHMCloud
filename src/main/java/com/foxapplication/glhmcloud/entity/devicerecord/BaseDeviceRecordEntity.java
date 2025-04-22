package com.foxapplication.glhmcloud.entity.devicerecord;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseDeviceRecordEntity {
    @Id
    private long time;//时间戳
    private String device_id = "";
    @Lob
    private String data = "";
    private String type = "";
}
