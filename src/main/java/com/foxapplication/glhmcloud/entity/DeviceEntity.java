package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class DeviceEntity {
    @Id
    private String id = "";
    private String type = "";
    private UUID organization_id = null ;
    private boolean online = false;
    @Lob
    private String name = "";
    private String image = "";
    @Lob
    private String info = "";
    private String device_description = "{}";
    @Lob
    private String description = "[]";//JSON

}
