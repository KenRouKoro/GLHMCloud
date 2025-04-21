package com.foxapplication.glhmcloud.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.core.client.MqttClientCreator;
import org.dromara.mica.mqtt.spring.client.event.MqttConnectedEvent;
import org.dromara.mica.mqtt.spring.client.event.MqttDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttClientConnectListener {

    private final MqttClientCreator mqttClientCreator;

    @Autowired
    public MqttClientConnectListener(MqttClientCreator mqttClientCreator) {
        this.mqttClientCreator = mqttClientCreator;
    }

    @EventListener
    public void onConnected(MqttConnectedEvent event) {
        log.info("MqttConnectedEvent:{}", event);
    }

    @EventListener
    public void onDisconnect(MqttDisconnectEvent event) {
        // 离线时更新重连时的密码，适用于类似阿里云 mqtt clientId 连接带时间戳的方式
        log.info("MqttDisconnectEvent:{}", event);
        // 在断线时更新 clientId、username、password
        //mqttClientCreator.clientId("newClient" + System.currentTimeMillis())
        //        .username("newUserName")
        //        .password("newPassword");
    }
}
