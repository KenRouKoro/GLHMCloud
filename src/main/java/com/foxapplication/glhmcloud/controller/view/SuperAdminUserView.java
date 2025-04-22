package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
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
@RequestMapping("/view/su")
@SaCheckRole("super-admin")
public class SuperAdminUserView {
    private final UserDao userDao;

    @Autowired
    public SuperAdminUserView(UserDao userDao) {
        this.userDao = userDao;
    }
    @GetMapping("/list")
    public BaseResponse<List<UserViewData>> list(){
        return BaseResponse.success(userDao.findAllSafe());
    }
    @GetMapping("/delete-user")
    public BaseResponse<String> delete(@RequestParam("user") String id){
        if(userDao.findById(UUID.fromString(id)).isEmpty())
            return BaseResponse.fail("用户不存在");
        userDao.deleteById(UUID.fromString(id));
        return BaseResponse.success();
    }
    @PostMapping("/update-organization-id")
    public BaseResponse<String> updateOrganizationID(@RequestParam("user")String user,@RequestParam("organization-id")String organizationID) {
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

    @PostMapping("/update-passwd")
    public BaseResponse<String> updatePW(@RequestParam("user")String user, @RequestParam("password")String password) {
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

    @PostMapping("/update-name")
    public BaseResponse<String> updateName(@RequestParam("user")String user,@RequestParam("name")String name) {
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
    @PostMapping("/update-email")
    public BaseResponse<String> updateEmail(@RequestParam("user")String user,@RequestParam("email")String email) {
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
    @PostMapping("/update-phone")
    public BaseResponse<String> updatePhone(@RequestParam("user")String user,@RequestParam("phone")String phone) {
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
    @PostMapping("/update-avatar")
    public BaseResponse<String> updateAvatar(@RequestParam("user")String user,@RequestParam("avatar")String avatar) {
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
    @PostMapping("/update-permission")
    public BaseResponse<Integer> updatePermission(@RequestParam("user")String user, @RequestParam("permission")int permission) {
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
    @PostMapping("/create-user")
    public BaseResponse<String> createUser(@RequestParam("user")String username){
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
