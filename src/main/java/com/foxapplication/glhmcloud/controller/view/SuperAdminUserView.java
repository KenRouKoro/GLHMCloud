package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import com.foxapplication.glhmcloud.param.view.UserViewData;
import com.foxapplication.glhmcloud.util.Check;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.hutool.core.lang.Validator;
import org.dromara.hutool.crypto.digest.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "超级管理员用户视图管理")
@RestController
@RequestMapping("/view/su")
@SaCheckRole("super-admin")
public class SuperAdminUserView {
    private final UserDao userDao;

    @Autowired
    public SuperAdminUserView(UserDao userDao) {
        this.userDao = userDao;
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    @Transactional
    public BaseResponse<List<UserViewData>> list(){
        return BaseResponse.success(userDao.findAllSafe());
    }

    @Operation(summary = "删除用户")
    @Transactional
    @GetMapping("/delete-user")
    public BaseResponse<String> delete(@Parameter(description = "用户ID") @RequestParam("user") String id){
        if(userDao.findById(UUID.fromString(id)).isEmpty())
            return BaseResponse.fail("用户不存在");
        userDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }

    @Operation(summary = "更新用户组织ID")
    @PostMapping("/update-organization-id")
    @Transactional
    public BaseResponse<String> updateOrganizationID(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "组织ID") @RequestParam("organization-id") String organizationID) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        }
        if (!Validator.isUUID(organizationID)){
            return BaseResponse.fail("组织ID格式错误");
        }
        userEntity.get().setOrganization_id(UUID.fromString(organizationID));
        userDao.saveAndFlush(userEntity.get());
        return BaseResponse.success(organizationID);
    }

    @Operation(summary = "更新用户密码")
    @PostMapping("/update-passwd")
    @Transactional
    public BaseResponse<String> updatePW(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新密码") @RequestParam("password") String password) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            String passwd = DigestUtil.sha512Hex(password);
            if(!Check.checkPassword(password)){
                return BaseResponse.fail("密码不符合要求");
            }
            userEntity.get().setPasswd(passwd);
            userDao.save(userEntity.get());
            return BaseResponse.success();
        }
    }

    @Operation(summary = "更新用户名称")
    @Transactional
    @PostMapping("/update-name")
    public BaseResponse<String> updateName(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新名称") @RequestParam("name") String name) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            userEntity.get().setUsername(name);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(name);
        }
    }

    @Operation(summary = "更新用户邮箱")
    @Transactional
    @PostMapping("/update-email")
    public BaseResponse<String> updateEmail(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新邮箱") @RequestParam("email") String email) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            if(!Validator.isEmail(email)){
                return BaseResponse.fail("邮箱格式错误");
            }
            userEntity.get().setEmail(email);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(email);
        }
    }

    @Operation(summary = "更新用户手机号")
    @Transactional
    @PostMapping("/update-phone")
    public BaseResponse<String> updatePhone(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新手机号") @RequestParam("phone") String phone) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            if (!Validator.isMobile(phone)){
                return BaseResponse.fail("手机号格式错误");
            }
            userEntity.get().setPhone(phone);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(phone);
        }
    }

    @Operation(summary = "更新用户头像")
    @Transactional
    @PostMapping("/update-avatar")
    public BaseResponse<String> updateAvatar(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新头像URL") @RequestParam("avatar") String avatar) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            userEntity.get().setAvatar(avatar);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(avatar);
        }
    }

    @Operation(summary = "更新用户权限")
    @Transactional
    @PostMapping("/update-permission")
    public BaseResponse<Integer> updatePermission(
            @Parameter(description = "用户ID") @RequestParam("user") String user,
            @Parameter(description = "新权限") @RequestParam("permission") int permission) {
        UUID id = UUID.fromString(user);
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            userEntity.get().setPermission(permission);
            userDao.saveAndFlush(userEntity.get());
        }
        return BaseResponse.success(permission);
    }

    @Operation(summary = "创建用户")
    @Transactional
    @PostMapping("/create-user")
    public BaseResponse<String> createUser(@Parameter(description = "用户名") @RequestParam("user") String username){
        if (userDao.existsByUsername(username)){
            return BaseResponse.fail("用户名已存在");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPermission(0);

        userEntity.setPasswd(DigestUtil.sha512Hex("Aa123456"));
        userDao.saveAndFlush(userEntity);
        return BaseResponse.success(userEntity.getId().toString());
    }
}