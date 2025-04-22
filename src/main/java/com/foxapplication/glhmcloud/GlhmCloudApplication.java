package com.foxapplication.glhmcloud;

import cn.dev33.satoken.SaManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.foxapplication.glhmcloud"})
@EnableScheduling // 启用定时任务
@Slf4j
public class GlhmCloudApplication {

    public static void main(String[] args)  throws JsonProcessingException {
        SpringApplication.run(GlhmCloudApplication.class, args);
        log.info("Sa-Token 配置：{}", SaManager.getConfig());
    }

}
