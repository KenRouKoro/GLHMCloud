package com.foxapplication.glhmcloud.service.impl;

import com.foxapplication.glhmcloud.dao.DeviceDao;
import com.foxapplication.glhmcloud.dao.DeviceRecordDao;
import com.foxapplication.glhmcloud.dao.DeviceSecRecordDao;
import com.foxapplication.glhmcloud.entity.DeviceEntity;
import com.foxapplication.glhmcloud.entity.DeviceRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.text.StrStripper;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.text.split.SplitUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.spring.client.MqttClientSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class MQTTServiceListener {
    private final DeviceDao deviceDao;
    private final DeviceRecordDao deviceRecordDao;
    private final DeviceSecRecordDao deviceSecRecordDao;

    @Autowired
    public MQTTServiceListener(DeviceDao deviceDao, DeviceRecordDao deviceRecordDao, DeviceSecRecordDao deviceSecRecordDao) {
        this.deviceDao = deviceDao;
        this.deviceRecordDao = deviceRecordDao;
        this.deviceSecRecordDao = deviceSecRecordDao;
    }

    @MqttClientSubscribe(value = "/hmcloud/drive/status/+", qos = MqttQoS.QOS1)
    public void onDriveStatus(String topic, byte[] payload){
        List<String> topicList = SplitUtil.split(topic, "/",true,true);
        String id = topicList.getLast();
        if (!deviceDao.existsById(id)){
            log.info("设备不存在：{}", id);
            return;
        }

        if (!deviceDao.isOnline(id)){
            DeviceEntity device = deviceDao.findById(id).get();
            device.setOnline(true);
            deviceDao.saveAndFlush(device);
        }

        String payloadStr = new String(payload, StandardCharsets.UTF_8);
        if (!JSONUtil.isTypeJSONObject(payloadStr)){
            log.info("ID:{} 数据格式错误：{}", id,payloadStr);
            return;
        }
        DeviceRecordEntity record = new DeviceRecordEntity();
        record.setDevice_id(id);
        record.setTime(System.currentTimeMillis());
        record.setType("status");
        record.setData(payloadStr);
        deviceSecRecordDao.saveAndFlush(record);
    }

    @MqttClientSubscribe(value = "$SYS/brokers/+/clients/+/disconnected", qos = MqttQoS.QOS1)
    public void offline(String topic, byte[] payload){
        String payloadStr = new String(payload, StandardCharsets.UTF_8);
        JSONObject payloadJson = JSONUtil.parse(payloadStr).asJSONObject();
        if (payloadJson.getStr("clientid","").startsWith("hmcloud_")){
            String id = payloadJson.getStr("clientid");
            if (deviceDao.existsById(id)){
                DeviceEntity device = deviceDao.findById(id).get();
                device.setOnline(false);
                deviceDao.saveAndFlush(device);
            }
        }
    }

    @Scheduled(fixedRate = 300000) // 300000毫秒 = 5分钟
    public void processFiveMinuteRecords() {
        // 获取所有在线设备列表
        List<DeviceEntity> onlineDevices = deviceDao.findAllByOnline(true);
        long now = System.currentTimeMillis();
        long fiveMinutesAgo = now - 5 * 60 * 1000;

        for (DeviceEntity device : onlineDevices) {
            String deviceId = device.getId();
            // 查询设备最近5分钟内的记录（时间戳范围 [fiveMinutesAgo, now]）
            List<DeviceRecordEntity> records = deviceRecordDao.findByIdBetween(fiveMinutesAgo, now , deviceId);

            // 调用自定义处理方法（当前留空，后续可补充逻辑）
            handleFiveMinuteRecords(records);
        }
    }

    // 额外方法处理
    private void handleFiveMinuteRecords(List<DeviceRecordEntity> records) {
        if (records.isEmpty()){
            return;
        }
        Map<String,Double> maxList = new HashMap<>();
        records.forEach(record -> {
            String data = record.getData();
            JSONObject dataJson = JSONUtil.parse(data).asJSONObject();
            dataJson.forEach((key, value) -> {
                if(value.asJSONPrimitive().isNumber()){
                    if (maxList.containsKey(key)){
                        double number = dataJson.getDouble(key);
                        if (number > maxList.get(key)){
                            maxList.put(key,number);
                        }
                    }
                }
            });
        });
        DeviceRecordEntity record = records.getLast();
        JSONObject dataJson = JSONUtil.parse(record.getData()).asJSONObject();
        dataJson.putAllValue(maxList);
        record.setData(dataJson.toString());
        deviceRecordDao.saveAndFlush(record);

    }
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldData() {
        Date yesterday = DateUtil.yesterday();
        deviceSecRecordDao.deleteByCreatedTimeBefore(yesterday.getTime());
    }

}