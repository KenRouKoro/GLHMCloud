package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue()
    private UUID id;

    private String username = "";
    @Lob
    private String passwd = "";
    private String email = "";
    private String phone = "";
    private String avatar = "";
    @Column(name = "organization_id")
    private UUID organization_id = null;
    private int permission = 0;
}