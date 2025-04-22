package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.DeviceDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.DeviceEntity;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/view/device")
public class DeviceView {
    private final DeviceDao deviceDao;
    private final UserDao userDao;

    @Autowired
    public DeviceView(DeviceDao deviceDao, UserDao userDao) {
        this.deviceDao = deviceDao;
        this.userDao = userDao;
    }

    @RequestMapping("/this")
    public BaseResponse<List<DeviceEntity>> getDevice() {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()){
            return BaseResponse.fail("用户不存在");
        }
        if (userEntity.get().getOrganization_id() == null){
            return BaseResponse.fail("用户未关联组织");
        }
        List<DeviceEntity> deviceEntityList;
        if (userEntity.get().getPermission() < 4){
            deviceEntityList = deviceDao.findAllByOrganization_id(userEntity.get().getOrganization_id());
        }else{
            deviceEntityList = deviceDao.findAll();
        }
        return BaseResponse.success(deviceEntityList);
    }
}
