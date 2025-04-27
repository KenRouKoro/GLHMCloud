package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class DeviceEntity {
    @Id
    private String id = "";
    @NotBlank
    private String type = "";
    @NotBlank
    private UUID organization_id = null ;
    private boolean online = false;
    @Lob
    @NotBlank
    private String name = "";
    private String image = "";
    @Lob
    private String info = "";
    @NotBlank
    private String device_description = "{}";
    @NotBlank
    @Lob
    private String description = "[]";//JSON

}
