package com.foxapplication.glhmcloud.controller;

import com.foxapplication.glhmcloud.param.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public BaseResponse<String> handlerException(Exception e) {
        log.error("异常: ", e);
        return BaseResponse.fail(e.getMessage());
    }
}
