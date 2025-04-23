package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.DeviceDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.DeviceEntity;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/view/device")
public class DeviceView {
    private final DeviceDao deviceDao;
    private final UserDao userDao;
    private final MqttClientTemplate mqttClientTemplate;

    @Autowired
    public DeviceView(DeviceDao deviceDao, UserDao userDao, MqttClientTemplate mqttClientTemplate) {
        this.deviceDao = deviceDao;
        this.userDao = userDao;
        this.mqttClientTemplate = mqttClientTemplate;
    }

    @PostMapping("/this")
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
    @PostMapping("/get")
    public BaseResponse<DeviceEntity> getDevice(String id) {
        Optional<DeviceEntity> deviceEntity = deviceDao.findById(id);
        if (deviceEntity.isEmpty()){
            return BaseResponse.fail("设备不存在");
        }
        return BaseResponse.success(deviceEntity.get());
    }
    @PostMapping("/list")
    @SaCheckRole("super-admin")
    public BaseResponse<List<DeviceEntity>> getDeviceList() {
        List<DeviceEntity> deviceEntityList = deviceDao.findAll();
        return BaseResponse.success(deviceEntityList);
    }
    @PostMapping("/delete")
    @SaCheckRole("super-admin")
    public BaseResponse<String> delete(String id) {
        deviceDao.deleteById(id);
        return BaseResponse.success();
    }
    @PostMapping("/update")
    @SaCheckRole("super-admin")
    public BaseResponse<String> update(DeviceEntity entity) {
        deviceDao.save(entity);
        return BaseResponse.success();
    }

    @PostMapping("/send-command")
    public BaseResponse<String> sendCommand(String deviceId, String command) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()){
            return BaseResponse.fail("用户不存在");
        }
        if (userEntity.get().getOrganization_id() == null){
            return BaseResponse.fail("用户未关联组织");
        }

        Optional<DeviceEntity> deviceEntity = deviceDao.findById(deviceId);
        if (deviceEntity.isEmpty()){
            return BaseResponse.fail("设备不存在");
        }

        if ((userEntity.get().getOrganization_id().equals(deviceEntity.get().getOrganization_id()) )&& (userEntity.get().getPermission()<4)){
            return BaseResponse.fail("设备不属于当前组织");
        }

        mqttClientTemplate.publish(StrUtil.format("/hmcloud/drive/command/{}",deviceId), command.getBytes(StandardCharsets.UTF_8));


        return BaseResponse.success();
    }


}
