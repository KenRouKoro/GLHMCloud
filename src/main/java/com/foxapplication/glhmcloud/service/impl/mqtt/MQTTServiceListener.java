package com.foxapplication.glhmcloud.service.impl.mqtt;

import com.foxapplication.glhmcloud.dao.DeviceDao;
import com.foxapplication.glhmcloud.dao.DeviceOperateDao;
import com.foxapplication.glhmcloud.dao.DeviceRecordDao;
import com.foxapplication.glhmcloud.dao.DeviceSecRecordDao;
import com.foxapplication.glhmcloud.entity.DeviceEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.BaseDeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceOperateEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceRecordEntity;
import com.foxapplication.glhmcloud.entity.devicerecord.DeviceSecRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.text.split.SplitUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.spring.client.MqttClientSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final DeviceOperateDao deviceOperateDao;

    /**
     * 构造函数，注入依赖的DAO组件。
     * @param deviceDao 设备信息DAO，用于操作设备实体
     * @param deviceRecordDao 设备记录DAO，用于存储和查询设备状态记录
     * @param deviceSecRecordDao 秒级记录DAO，处理设备秒级状态相关的记录
     * @param deviceOperateDao 操作记录DAO，记录设备操作指令
     */
    @Autowired
    public MQTTServiceListener(DeviceDao deviceDao, DeviceRecordDao deviceRecordDao, 
            DeviceSecRecordDao deviceSecRecordDao, DeviceOperateDao deviceOperateDao) {
        this.deviceDao = deviceDao;
        this.deviceRecordDao = deviceRecordDao;
        this.deviceSecRecordDao = deviceSecRecordDao;
        this.deviceOperateDao = deviceOperateDao;
    }

    /**
     * 处理设备状态更新消息。
     * @param topic MQTT主题，格式为/hmcloud/drive/status/{deviceId}
     * @param payload 设备状态的JSON数据，包含设备状态信息
     */
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
        DeviceSecRecordEntity record = new DeviceSecRecordEntity();
        record.setDevice_id(id);
        record.setTime(System.currentTimeMillis());
        record.setType("status");
        record.setData(payloadStr);
        deviceSecRecordDao.saveAndFlush(record);
    }

    /**
     * 处理设备操作指令消息。
     * @param topic MQTT主题，格式为/hmcloud/drive/operate/{deviceId}
     * @param payload 操作指令的JSON数据，包含具体的操作参数
     */
    @MqttClientSubscribe(value = "/hmcloud/drive/operate/+", qos = MqttQoS.QOS1)
    public void onDriveOperate(String topic, byte[] payload){
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

        DeviceOperateEntity record = new DeviceOperateEntity();
        record.setDevice_id(id);
        record.setTime(System.currentTimeMillis());
        record.setType("operate");
        record.setData(payloadStr);
        deviceOperateDao.saveAndFlush(record);
    }

    /**
     * 处理设备离线通知。
     * @param topic MQTT系统主题，格式为$SYS/brokers/+/clients/+/disconnected
     * @param payload 离线事件的JSON数据，包含clientid等信息
     */
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

    /**
     * 每5分钟执行一次的定时任务，处理设备记录数据。
     * 该任务会检查所有在线设备的最近5分钟记录，并执行相应的处理逻辑。
     */
    @Scheduled(fixedRate = 300000)
    public void processFiveMinuteRecords() {
        // 获取所有在线设备列表
        List<DeviceEntity> onlineDevices = deviceDao.findAllByOnline(true);
        long now = System.currentTimeMillis();
        long fiveMinutesAgo = now - 5 * 60 * 1000;

        for (DeviceEntity device : onlineDevices) {
            String deviceId = device.getId();
            // 查询设备最近5分钟内的记录（时间戳范围 [fiveMinutesAgo, now]）
            List<BaseDeviceRecordEntity> records = Collections.unmodifiableList(deviceSecRecordDao.findByIdBetween(fiveMinutesAgo, now, deviceId));

            // 调用自定义处理方法
            handleFiveMinuteRecords(records);
        }
    }
    @Scheduled(fixedRate = 120000)
    public void checkOnline(){
        List<DeviceEntity> onlineDevices = deviceDao.findAllByOnline(true);
        long now = System.currentTimeMillis();
        long fiveMinutesAgo = now - 2 * 60 * 1000;
        for (DeviceEntity device : onlineDevices) {
            String deviceId = device.getId();
            if(!deviceSecRecordDao.existsByDevice_idAndTime(deviceId,fiveMinutesAgo,now)){
                device.setOnline(false);
                deviceDao.saveAndFlush(device);
            }
        }
    }

    /**
     * 处理5分钟内的设备记录数据，计算各字段的最大值并更新记录。
     * @param records 需要处理的设备记录列表，按时间排序
     */
    private void handleFiveMinuteRecords(List<BaseDeviceRecordEntity> records) {
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
                    }else{
                        maxList.put(key,dataJson.getDouble(key));
                    }
                }
            });
        });
        DeviceRecordEntity record = (DeviceRecordEntity) records.getLast();
        JSONObject dataJson = JSONUtil.parse(record.getData()).asJSONObject();
        dataJson.putAllValue(maxList);
        record.setData(dataJson.toString());
        deviceRecordDao.saveAndFlush(record);

    }

    /**
     * 每天凌晨2点执行的定时任务，清理过期数据。
     * 清理条件为创建时间早于一周前的数据。
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldData() {
        Date yesterday = DateUtil.lastWeek();
        deviceSecRecordDao.deleteByCreatedTimeBefore(yesterday.getTime());
    }

}