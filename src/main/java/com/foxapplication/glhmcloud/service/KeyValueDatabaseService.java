package com.foxapplication.glhmcloud.service;

import org.dromara.hutool.core.lang.getter.TypeGetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface KeyValueDatabaseService extends TypeGetter<String> {
    /**
     * 存储键值对
     * @param key 键
     * @param value 值
     */
    @Transactional
    void put(String key , Object value);
}
