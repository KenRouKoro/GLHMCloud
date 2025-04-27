package com.foxapplication.glhmcloud.controller.view;

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

@Tag(name = "用户视图管理")
@RestController
@RequestMapping("/view/user")
public class UserView {
    private final UserDao userDao;

    @Autowired
    public UserView(UserDao userDao) {
        this.userDao = userDao;
    }

    @Operation(summary = "获取当前用户信息")
    @Transactional
    @GetMapping("/this")
    public BaseResponse<UserViewData> getUserInfo() {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
    }

    @Operation(summary = "检查密码是否为默认密码")
    @GetMapping("/check-password")
    @Transactional
    public BaseResponse<Boolean> checkPassword() {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(!userEntity.get().getPasswd().equals(DigestUtil.sha512Hex("Aa123456")));
        }
    }

    @Operation(summary = "根据用户名获取用户信息")
    @Transactional
    @PostMapping("/name")
    public BaseResponse<UserViewData> getUserInfo(@Parameter(description = "用户名") @RequestParam("username") String username) {
        Optional<UserEntity> userEntity = userDao.findByUsername(username);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }

    @Operation(summary = "根据用户ID获取用户信息")
    @Transactional
    @PostMapping("/id")
    public BaseResponse<UserViewData> getUserInfoByID(@Parameter(description = "用户ID") @RequestParam("id") String id) {
        Optional<UserEntity> userEntity = userDao.findById(UUID.fromString(id));
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }

    @Operation(summary = "根据邮箱获取用户信息")
    @PostMapping("/email")
    @Transactional
    public BaseResponse<UserViewData> getUserInfoByEmail(@Parameter(description = "邮箱") @RequestParam("email") String email) {
        Optional<UserEntity> userEntity = userDao.findByEmail(email);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }

    @Operation(summary = "根据手机号获取用户信息")
    @Transactional
    @PostMapping("/phone")
    public BaseResponse<UserViewData> getUserInfoByPhone(@Parameter(description = "手机号") @RequestParam("phone") String phone) {
        Optional<UserEntity> userEntity = userDao.findByPhone(phone);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }

    @Operation(summary = "根据组织ID获取用户信息列表")
    @Transactional
    @PostMapping("/organization-id")
    public BaseResponse<List<UserViewData>> getUserInfoByOrganizationID(@Parameter(description = "组织ID") @RequestParam("organization-id") String organizationID) {
        List<UserViewData> list = userDao.findAllByOrganization_idSafe(UUID.fromString(organizationID));
        return BaseResponse.success(list);
    }

    @Operation(summary = "更新用户密码")
    @Transactional
    @PostMapping("/update-passwd")
    public BaseResponse<String> updatePW(
            @Parameter(description = "旧密码") @RequestParam("old-password") String oldPassword,
            @Parameter(description = "新密码") @RequestParam("password") String password) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            String oldPasswd = DigestUtil.sha512Hex(oldPassword);
            String passwd = DigestUtil.sha512Hex(password);
            if (userEntity.get().getPasswd().equals(oldPasswd)) {
                if (!Check.checkPassword(password)) {
                    return BaseResponse.fail("密码不符合要求");
                }
                userEntity.get().setPasswd(passwd);
                userDao.save(userEntity.get());
                return BaseResponse.success();
            } else {
                return BaseResponse.fail("密码错误");
            }
        }
    }

    @Operation(summary = "更新用户名称")
    @PostMapping("/update-name")
    @Transactional
    public BaseResponse<String> updateName(@Parameter(description = "新名称") @RequestParam("name") String name) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
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
    public BaseResponse<String> updateEmail(@Parameter(description = "新邮箱") @RequestParam("email") String email) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            if (!Validator.isEmail(email)) {
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
    public BaseResponse<String> updatePhone(@Parameter(description = "新手机号") @RequestParam("phone") String phone) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            if (!Validator.isMobile(phone)) {
                return BaseResponse.fail("手机号格式错误");
            }
            userEntity.get().setPhone(phone);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(phone);
        }
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/update-avatar")
    @Transactional
    public BaseResponse<String> updateAvatar(@Parameter(description = "新头像URL") @RequestParam("avatar") String avatar) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            userEntity.get().setAvatar(avatar);
            userDao.saveAndFlush(userEntity.get());
            return BaseResponse.success(avatar);
        }
    }
}