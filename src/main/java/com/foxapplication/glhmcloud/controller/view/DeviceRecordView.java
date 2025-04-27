package com.foxapplication.glhmcloud.controller.view;

import com.foxapplication.glhmcloud.dao.DeviceOperateDao;
import com.foxapplication.glhmcloud.dao.DeviceRecordDao;
import com.foxapplication.glhmcloud.dao.DeviceSecRecordDao;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceOperateEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceSecRecordEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "设备记录视图管理")
@RestController
@RequestMapping("/view/record")
public class DeviceRecordView {
    private final DeviceSecRecordDao deviceSecRecordDao;
    private final DeviceRecordDao deviceRecordDao;
    private final DeviceOperateDao deviceOperateDao;

    @Autowired
    public DeviceRecordView(DeviceSecRecordDao deviceSecRecordDao, DeviceRecordDao deviceRecordDao, DeviceOperateDao deviceOperateDao) {
        this.deviceSecRecordDao = deviceSecRecordDao;
        this.deviceRecordDao = deviceRecordDao;
        this.deviceOperateDao = deviceOperateDao;
    }

    @Operation(summary = "获取设备记录")
    @PostMapping("/device")
    @Transactional
    public BaseResponse<List<DeviceRecordEntity>> getDeviceRecord(
            @Parameter(description = "设备ID") @RequestParam("id") String id,
            @Parameter(description = "页码") @RequestParam("page") int page,
            @Parameter(description = "每页大小") @RequestParam("size") int size) {
        Page<DeviceRecordEntity> deviceRecordEntity = deviceRecordDao.findAllByOrderByTimeDesc(PageRequest.of(page, size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }

    @Operation(summary = "获取设备操作记录")
    @Transactional
    @PostMapping("/operate")
    public BaseResponse<List<DeviceOperateEntity>> getDeviceOperateRecord(
            @Parameter(description = "设备ID") @RequestParam("id") String id,
            @Parameter(description = "页码") @RequestParam("page") int page,
            @Parameter(description = "每页大小") @RequestParam("size") int size) {
        Page<DeviceOperateEntity> deviceRecordEntity = deviceOperateDao.findAllByOrderByTimeDesc(PageRequest.of(page, size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }

    @Operation(summary = "获取设备秒级记录")
    @PostMapping("/sec")
    @Transactional
    public BaseResponse<List<DeviceSecRecordEntity>> getDeviceSecRecord(
            @Parameter(description = "设备ID") @RequestParam("id") String id,
            @Parameter(description = "页码") @RequestParam("page") int page,
            @Parameter(description = "每页大小") @RequestParam("size") int size) {
        Page<DeviceSecRecordEntity> deviceRecordEntity = deviceSecRecordDao.findAllByOrderByTimeDesc(PageRequest.of(page, size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }

    @Operation(summary = "获取设备记录数量")
    @Transactional
    @PostMapping("/device/count")
    public BaseResponse<Long> getDeviceRecordCount(
            @Parameter(description = "设备ID") @RequestParam("id") String id) {
        long count = deviceRecordDao.countByDevice_id(id);
        return BaseResponse.success(count);
    }

    @Operation(summary = "获取设备操作记录数量")
    @Transactional
    @PostMapping("/operate/count")
    public BaseResponse<Long> getDeviceOperateRecordCount(
            @Parameter(description = "设备ID") @RequestParam("id") String id) {
        long count = deviceOperateDao.countByDevice_id(id);
        return BaseResponse.success(count);
    }

    @Operation(summary = "获取设备秒级记录数量")
    @PostMapping("/sec/count")
    @Transactional
    public BaseResponse<Long> getDeviceSecRecordCount(
            @Parameter(description = "设备ID") @RequestParam("id") String id) {
        long count = deviceSecRecordDao.countByDevice_id(id);
        return BaseResponse.success(count);
    }
}