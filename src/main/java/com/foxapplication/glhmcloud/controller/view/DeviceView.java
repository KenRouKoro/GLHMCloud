package com.foxapplication.glhmcloud.controller.view;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.foxapplication.glhmcloud.dao.DeviceDao;
import com.foxapplication.glhmcloud.dao.UserDao;
import com.foxapplication.glhmcloud.entity.DeviceEntity;
import com.foxapplication.glhmcloud.entity.UserEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "设备视图管理")
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

    @Operation(summary = "获取当前用户的设备列表")
    @Transactional
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
    @Operation(summary = "根据设备ID获取设备信息")
    @Transactional
    @PostMapping("/get")
    public BaseResponse<DeviceEntity> getDevice(@Parameter(description = "设备ID") @RequestParam("id") String id) {
        Optional<DeviceEntity> deviceEntity = deviceDao.findById(id);
        if (deviceEntity.isEmpty()){
            return BaseResponse.fail("设备不存在");
        }
        return BaseResponse.success(deviceEntity.get());
    }
    @Operation(summary = "获取所有设备列表（仅限超级管理员）")
    @PostMapping("/list")
    @SaCheckRole("super-admin")
    @Transactional
    public BaseResponse<List<DeviceEntity>> getDeviceList() {
        List<DeviceEntity> deviceEntityList = deviceDao.findAll();
        return BaseResponse.success(deviceEntityList);
    }
    @Operation(summary = "删除设备（仅限超级管理员）")
    @PostMapping("/delete")
    @Transactional
    @SaCheckRole("super-admin")
    public BaseResponse<String> delete(@Parameter(description = "设备ID") @RequestParam("id") String id) {
        deviceDao.deleteById(id);
        return BaseResponse.success();
    }
    @Operation(summary = "更新设备信息（仅限超级管理员）")
    @PostMapping("/update")
    @Transactional
    @SaCheckRole("super-admin")
    public BaseResponse<String> update(@Parameter(description = "设备实体") @ModelAttribute DeviceEntity entity) {
        deviceDao.save(entity);
        return BaseResponse.success();
    }

    @Operation(summary = "向设备发送命令")
    @PostMapping("/send-command")
    @Transactional
    public BaseResponse<String> sendCommand(
            @Parameter(description = "设备ID") @RequestParam("deviceId") String deviceId,
            @Parameter(description = "命令内容") @RequestParam("command") String command) {
        UUID id = UUID.fromString(StpUtil.getLoginIdAsString());
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isEmpty()){
            return BaseResponse.fail("用户不存在");
        }
        if (userEntity.get().getOrganization_id() == null){
            return BaseResponse.fail("用户未关联组织");
        }

        if (userEntity.get().getPermission() < 2){
            return BaseResponse.fail("用户无权限");
        }

        Optional<DeviceEntity> deviceEntity = deviceDao.findById(deviceId);
        if (deviceEntity.isEmpty()){
            return BaseResponse.fail("设备不存在");
        }

        if ((userEntity.get().getOrganization_id().equals(deviceEntity.get().getOrganization_id()) )&& (userEntity.get().getPermission()<4)){
            return BaseResponse.fail("设备不属于当前组织");
        }

        if(command.startsWith("lock")){
            if (userEntity.get().getPermission() < 4){
                return BaseResponse.fail("用户无权限");
            }
        }

        mqttClientTemplate.publish(StrUtil.format("/hmcloud/drive/command/{}",deviceId), command.getBytes(StandardCharsets.UTF_8));


        return BaseResponse.success();
    }


}
