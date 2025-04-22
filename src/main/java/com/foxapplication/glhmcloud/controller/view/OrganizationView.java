package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.OrganizationDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.OrganizationEntity;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/view/organization")
public class OrganizationView {
    private final OrganizationDao organizationDao;
    private final UserDao userDao;
    @Autowired
    public OrganizationView(OrganizationDao organizationDao, UserDao userDao) {
        this.organizationDao = organizationDao;
        this.userDao = userDao;
    }

    @PostMapping("/this")
    public BaseResponse<OrganizationEntity> getOrganization() {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            if (userEntity.get().getOrganization_id() == null ) {
                return BaseResponse.fail("用户未关联组织");
            } else {
                Optional<OrganizationEntity> organizationEntity = organizationDao.findById(userEntity.get().getOrganization_id());
                if (organizationEntity.isEmpty()) {
                    return BaseResponse.fail("组织不存在");
                } else {
                    return BaseResponse.success(organizationEntity.get());
                }
            }
        }

    }
    @PostMapping("/list")
    @SaCheckRole("super-admin")
    public BaseResponse<List<OrganizationEntity>> list() {
        return BaseResponse.success(organizationDao.findAll());
    }
    @PostMapping("/delete")
    @SaCheckRole("super-admin")
    public BaseResponse<String> delete(String id) {
        organizationDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }
    @PostMapping("/update")
    @SaCheckRole("super-admin")
    public BaseResponse<String> update(@RequestParam("organization") OrganizationEntity entity) {

        organizationDao.saveAndFlush(entity);
        return BaseResponse.success();
    }
}
