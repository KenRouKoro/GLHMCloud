package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ImageDTO extends JpaRepository<ImageEntity, UUID> {
    @Query("SELECT u.id FROM ImageEntity u")
    List<UUID> findIds();
}
