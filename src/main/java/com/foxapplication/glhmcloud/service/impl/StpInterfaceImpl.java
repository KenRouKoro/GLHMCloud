package com.foxapplication.glhmcloud.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.UserEntity;
import org.dromara.hutool.core.convert.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class StpInterfaceImpl implements StpInterface {

    private final UserDao userDao;

    @Autowired
    public StpInterfaceImpl(UserDao userDao){
        this.userDao = userDao;
    }
    /**
     * 返回指定账号id所拥有的权限码集合
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 该账号id具有的权限码集合
     */
    @Override
    @Transactional
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 该账号id具有的角色标识集合
     */
    @Override
    @Transactional
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        list.add("user");
        String userID = Converter.identity().convert(String.class, loginId);
        UserEntity user = userDao.findById(UUID.fromString(userID)).orElse(null);
        if (user != null){
            if (user.getPermission() >= 2){
                list.add("admin");
            }
            if (user.getPermission() >= 4){
                list.add("super-admin");
            }
        }
        return list;
    }
}
