package com.foxapplication.glhmcloud.controller;

import cn.dev33.satoken.exception.NotLoginException;
import com.foxapplication.glhmcloud.param.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Tag(name = "全局异常处理")
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Operation(summary = "处理全局异常")
    @ExceptionHandler
    @Transactional
    public BaseResponse<String> handlerException(Exception e) {
        if (e instanceof NotLoginException) {
            log.error("未登录请求");
            return BaseResponse.fail("请先登录");
        }
        log.error("异常: ", e);
        return BaseResponse.fail(e.getMessage());
    }
}