package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyValueDao extends JpaRepository<KeyValueEntity, String> {
}
