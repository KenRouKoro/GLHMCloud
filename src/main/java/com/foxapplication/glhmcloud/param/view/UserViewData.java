package com.foxapplication.glhmcloud.param.view;

import com.foxapplication.glhmcloud.entity.UserEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserViewData {
    private UUID id;
    private String username = "";
    private String email = "";
    private String phone = "";
    private String organizationID = "";
    private String avatar = "";
    private int permission = 0;

    public UserViewData(UserEntity entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.organizationID = String.valueOf(entity.getOrganization_id());
        this.permission = entity.getPermission();
        this.avatar= entity.getAvatar();
    }

    public static UserViewData fromEntity(UserEntity entity) {
        UserViewData data = new UserViewData();
        data.id = entity.getId();
        data.username = entity.getUsername();
        data.email = entity.getEmail();
        data.phone = entity.getPhone();
        data.organizationID = String.valueOf(entity.getOrganization_id());
        data.permission = entity.getPermission();
        data.avatar= entity.getAvatar();
        return data;
    }
}
