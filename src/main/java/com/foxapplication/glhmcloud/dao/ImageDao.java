package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.ImageEntity;
import com.foxapplication.glhmcloud.param.ImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ImageDao extends JpaRepository<ImageEntity, UUID> {
    @Query("SELECT new com.foxapplication.glhmcloud.param.ImageProjection(u.id, u.name) FROM ImageEntity u")
    List<ImageProjection> findAllIdAndName();
}
