package com.foxapplication.glhmcloud.controller.auth;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.OrganizationDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import com.foxapplication.glhmcloud.service.KeyValueDatabaseService;
import com.foxapplication.glhmcloud.util.Check;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.crypto.asymmetric.KeyType;
import org.dromara.hutool.crypto.asymmetric.RSA;
import org.dromara.hutool.crypto.digest.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "认证管理")
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static RSA rsa = null;
    private final UserDao userDao;
    private final OrganizationDao organizationDao;
    private final KeyValueDatabaseService keyValueDatabaseService;

    @Autowired
    public AuthController(UserDao userDao, OrganizationDao organizationDao, KeyValueDatabaseService keyValueDatabaseService) {
        this.userDao = userDao;
        this.organizationDao = organizationDao;
        this.keyValueDatabaseService = keyValueDatabaseService;
        if (rsa == null) {
            rsa = new RSA();
        }
    }

    /**
     * 登陆后session处理
     */
    private void loginFinishCallBack(UserEntity user) {
        SaSession session = StpUtil.getSession();
        session.set("user", user);
        if (user.getOrganization_id() != null) {
            session.set("organization", organizationDao.findById(user.getOrganization_id()).orElse(null));
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @Transactional
    public BaseResponse<String> doLogin(
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "加密后的密码") @RequestParam("password") String password64,
            @Parameter(description = "是否记住登录状态") @RequestParam(value = "remember", defaultValue = "false") boolean remember) {
        Optional<UserEntity> userEntity = userDao.findByUsername(username);
        if (userEntity.isEmpty()) {
            return BaseResponse.fail("用户不存在");
        }
        String password = rsa.decryptStr(password64, KeyType.PrivateKey);
        String passwd = DigestUtil.sha512Hex(password);
        if (passwd.equals(userEntity.get().getPasswd())) {
            StpUtil.login(userEntity.get().getId().toString(), remember);
            loginFinishCallBack(userEntity.get());
            return BaseResponse.success(StpUtil.getTokenValue());
        } else {
            return BaseResponse.fail("密码错误");
        }
    }

    @Operation(summary = "获取RSA公钥")
    @GetMapping("/publicKey")
    @Transactional
    public BaseResponse<String> getPublicKey() {
        String publicKey = rsa.getPublicKeyBase64();
        return BaseResponse.success(publicKey);
    }

    @Operation(summary = "检查用户是否登录")
    @GetMapping("/isLogin")
    @Transactional
    public BaseResponse<Boolean> isLogin() {
        return BaseResponse.success(StpUtil.isLogin());
    }

    @Operation(summary = "用户登出")
    @GetMapping("/logout")
    @Transactional
    public BaseResponse<String> logout() {
        StpUtil.logout();
        return BaseResponse.success();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @Transactional
    public BaseResponse<String> register(
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "加密后的密码") @RequestParam("password") String password64) {
        if (!keyValueDatabaseService.getBool("can-register", false)) {
            if (userDao.count() != 0) {
                return BaseResponse.fail("注册已关闭");
            }
        }
        if (username.length() < 4 || username.length() > 32) {
            return BaseResponse.fail("用户名长度必须为4-32位");
        }
        if (userDao.existsByUsername(username)) {
            return BaseResponse.fail("用户名已存在");
        }
        log.info("{}", password64);
        String password = rsa.decryptStr(password64, KeyType.PrivateKey);

        if (!Check.checkPassword(password)) {
            return BaseResponse.fail("密码不符合要求");
        }

        String passwd = DigestUtil.sha512Hex(password);
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPasswd(passwd);
        if (userDao.count() <= 0) {
            userEntity.setPermission(4);
        }
        userEntity = userDao.saveAndFlush(userEntity);
        StpUtil.login(userEntity.getId().toString());
        return BaseResponse.success(StpUtil.getTokenValue());
    }

    @Operation(summary = "检查是否允许注册")
    @GetMapping("/can-register")
    @Transactional
    public BaseResponse<Boolean> canRegister() {
        if (userDao.count() < 1) {
            return BaseResponse.success(true);
        }
        return BaseResponse.success(keyValueDatabaseService.getBool("can-register", false));
    }
}
