package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.OrganizationDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.OrganizationEntity;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "组织视图管理")
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

    @Operation(summary = "获取当前用户所属组织")
    @Transactional
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

    @Operation(summary = "获取所有组织列表")
    @Transactional
    @PostMapping("/list")
    public BaseResponse<List<OrganizationEntity>> list() {
        return BaseResponse.success(organizationDao.findAll());
    }

    @Operation(summary = "删除组织")
    @PostMapping("/delete")
    @SaCheckRole("super-admin")
    @Transactional
    public BaseResponse<String> delete(@Parameter(description = "组织ID") @RequestParam("id") String id) {
        organizationDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }

    @Operation(summary = "更新组织信息")
    @PostMapping("/update")
    @SaCheckRole("super-admin")
    @Transactional
    public BaseResponse<String> update(@Parameter(description = "组织实体") @ModelAttribute OrganizationEntity entity) {
        organizationDao.save(entity);
        return BaseResponse.success();
    }

    @Operation(summary = "获取组织名称")
    @Transactional
    @PostMapping("/get-name")
    public BaseResponse<String> getName(@Parameter(description = "组织ID") @RequestParam("id") String id) {
        Optional<OrganizationEntity> organizationEntity = organizationDao.findById(UUID.fromString(id));
        if (organizationEntity.isEmpty()) {
            return BaseResponse.fail("组织不存在");
        } else {
            return BaseResponse.success(organizationEntity.get().getName());
        }
    }
}