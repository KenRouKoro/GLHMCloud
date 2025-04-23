package com.foxapplication.glhmcloud.controller.view;

import com.foxapplication.glhmcloud.dao.DeviceOperateDao;
import com.foxapplication.glhmcloud.dao.DeviceRecordDao;
import com.foxapplication.glhmcloud.dao.DeviceSecRecordDao;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceOperateEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceSecRecordEntity;
import com.foxapplication.glhmcloud.param.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/device")
    public BaseResponse<List<DeviceRecordEntity>> getDeviceRecord(@RequestParam("id")String id, @RequestParam("page")int page, @RequestParam("size")int size) {
        Page<DeviceRecordEntity> deviceRecordEntity = deviceRecordDao.findAllByOrderByTimeDesc(PageRequest.of(page,size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }
    @PostMapping("/operate")
    public BaseResponse<List<DeviceOperateEntity>> getDeviceOperateRecord(@RequestParam("id")String id, @RequestParam("page")int page, @RequestParam("size")int size) {
        Page<DeviceOperateEntity> deviceRecordEntity = deviceOperateDao.findAllByOrderByTimeDesc(PageRequest.of(page,size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }
    @PostMapping("/sec")
    public BaseResponse<List<DeviceSecRecordEntity>> getDeviceSecRecord(@RequestParam("id")String id, @RequestParam("page")int page, @RequestParam("size")int size) {
        Page<DeviceSecRecordEntity> deviceRecordEntity = deviceSecRecordDao.findAllByOrderByTimeDesc(PageRequest.of(page,size), id);
        return BaseResponse.success(deviceRecordEntity.getContent());
    }
}
