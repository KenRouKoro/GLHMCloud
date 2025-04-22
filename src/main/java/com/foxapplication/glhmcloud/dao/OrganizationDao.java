package com.foxapplication.glhmcloud.dao;

import com.foxapplication.glhmcloud.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationDao extends JpaRepository<OrganizationEntity, UUID> {
}
