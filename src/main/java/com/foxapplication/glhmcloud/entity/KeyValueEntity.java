package com.foxapplication.glhmcloud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "key_value_store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueEntity {
    @Id          // 主键
    @Column(name = "storage_key", nullable = false, unique = true)
    private String key;
    @Lob
    @Column(name = "storage_value")
    private String value;
}
