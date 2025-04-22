package com.foxapplication.glhmcloud.service.impl.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.core.client.MqttClientCreator;
import org.dromara.mica.mqtt.spring.client.event.MqttConnectedEvent;
import org.dromara.mica.mqtt.spring.client.event.MqttDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQTTClientConnectListener {

    private final MqttClientCreator mqttClientCreator;

    @Autowired
    public MQTTClientConnectListener(MqttClientCreator mqttClientCreator) {
        this.mqttClientCreator = mqttClientCreator;
    }

    @EventListener
    public void onConnected(MqttConnectedEvent event) {
        log.info("MqttConnectedEvent:{}", event);
    }

    @EventListener
    public void onDisconnect(MqttDisconnectEvent event) {
        log.info("MqttDisconnectEvent:{}", event);
    }
}
