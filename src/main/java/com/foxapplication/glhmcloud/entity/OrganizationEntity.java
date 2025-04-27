package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class OrganizationEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank
    private String name = "";
    private String phone = "";
    private String email = "";
    @Lob
    private String address = "";
    @Lob
    private String description = "";
    /**
     * JSON Array
     */
    @Lob
    private String deviceEntities = "[]";
}
