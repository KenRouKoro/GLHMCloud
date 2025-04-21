package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyValueDTO extends JpaRepository<KeyValueEntity, String> {
}
