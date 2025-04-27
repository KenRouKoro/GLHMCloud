package com.foxapplication.glhmcloud.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            try{
                if (SaHolder.getContext().getRequest().getMethod().equals("OPTIONS")){
                    return;
                }
                StpUtil.checkLogin();
            } catch (NotLoginException e){
                if (SaHolder.getContext().getRequest().getUrl().contains("/image")){
                    return;
                }
                 log.error("未登录请求：{}",SaHolder.getContext().getRequest().getUrl());
                 throw e;
            }
        }))
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**","/pub/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 应用到所有路径
                //.allowedOrigins("http://localhost:5173","https://hmcloud.korostudio.cn") // 允许的来源域名（替换为你的前端地址）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 允许携带凭证（如Cookie）
                .maxAge(3600); // 预检请求缓存时间（单位：秒）
    }

}
