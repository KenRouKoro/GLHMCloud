package com.foxapplication.glhmcloud.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hutool.core.convert.Converter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "success", data);
    }
    public static <T> BaseResponse<T> success(String message , T data) {
        return new BaseResponse<>(200, message, data);
    }
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(200, "success", null);
    }
    public static <T> BaseResponse<T> fail() {
        return new BaseResponse<>(500, "fail", null);
    }
    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<>(500, message, null);
    }
    public static <T> BaseResponse<T> fail(String message,T data) {
        return new BaseResponse<>(500, message, data);
    }
    public static <T> BaseResponse<T> fail(int code, String message) {
        return new BaseResponse<>(code, message, null);
    }
    public static <T> BaseResponse<T> fail(int code, String message, T data) {
        return new BaseResponse<>(code, message, data);
    }
}
