package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class ImageEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="image", columnDefinition="longblob", nullable=false)
    private byte[] image;

}
