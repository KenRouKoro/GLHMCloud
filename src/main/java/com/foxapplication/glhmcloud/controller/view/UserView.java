package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import com.foxapplication.glhmcloud.param.view.UserViewData;
import com.foxapplication.glhmcloud.util.Check;
import org.dromara.hutool.core.lang.Validator;
import org.dromara.hutool.crypto.digest.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/view/user")
public class UserView {
    private final UserDao userDao;

    @Autowired
    public UserView(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/this")
    public BaseResponse<UserViewData> getUserInfo() {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
    }
    @GetMapping("/check-password")
    public BaseResponse<Boolean>checkPassword(){
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(!userEntity.get().getPasswd().equals(DigestUtil.sha512Hex("Aa123456")));
        }
    }

    @PostMapping("/name")
    public BaseResponse<UserViewData> getUserInfo(@RequestParam("username") String username) {
        Optional<UserEntity> userEntity = userDao.findByUsername(username);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }
    @PostMapping("/id")
    public BaseResponse<UserViewData> getUserInfoByID(@RequestParam("id")String id) {
        Optional<UserEntity> userEntity = userDao.findById(UUID.fromString(id));
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }
    @PostMapping("/email")
    public BaseResponse<UserViewData> getUserInfoByEmail(@RequestParam("email")String email) {
        Optional<UserEntity> userEntity = userDao.findByEmail(email);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }
    @PostMapping("/phone")
    public BaseResponse<UserViewData> getUserInfoByPhone(@RequestParam("phone")String phone) {
        Optional<UserEntity> userEntity = userDao.findByPhone(phone);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            return BaseResponse.success(UserViewData.fromEntity(userEntity.get()));
        }
    }
    @PostMapping("/organization-id")
    public BaseResponse<List<UserViewData>> getUserInfoByOrganizationID(@RequestParam("organization-id")String organizationID) {
        List<UserViewData> list = userDao.findAllByOrganization_idSafe(UUID.fromString(organizationID));
        return BaseResponse.success(list);
    }

    @PostMapping("/update-passwd")
    public BaseResponse<String> updatePW(@RequestParam("old-password") String oldPassword, @RequestParam("password")String password) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        } else {
            String oldPasswd = DigestUtil.sha512Hex(oldPassword);
            String passwd = DigestUtil.sha512Hex(password);
            if (userEntity.get().getPasswd().equals(oldPasswd)) {
                if(!Check.checkPassword(password)){
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

    @PostMapping("/update-name")
    public BaseResponse<String> updateName(@RequestParam("name")String name) {
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
    @PostMapping("/update-email")
    public BaseResponse<String> updateEmail(@RequestParam("email")String email) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
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
    @PostMapping("/update-phone")
    public BaseResponse<String> updatePhone(@RequestParam("phone")String phone) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
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
    @PostMapping("/update-avatar")
    public BaseResponse<String> updateAvatar(@RequestParam("avatar")String avatar) {
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
