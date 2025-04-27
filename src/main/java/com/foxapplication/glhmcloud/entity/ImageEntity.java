package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class ImageEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank
    private String name;
    @Lob
    @NotBlank
    private byte[] image;

}
