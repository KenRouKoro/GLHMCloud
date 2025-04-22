package com.foxapplication.glhmcloud.service;

import org.dromara.hutool.core.lang.getter.TypeGetter;
import org.springframework.stereotype.Service;

@Service
public interface KeyValueDatabaseService extends TypeGetter<String> {
    /**
     * 存储键值对
     * @param key 键
     * @param value 值
     */
    void put(String key , Object value);
}
