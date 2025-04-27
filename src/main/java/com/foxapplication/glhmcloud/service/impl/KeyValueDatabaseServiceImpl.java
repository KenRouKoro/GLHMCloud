package com.foxapplication.glhmcloud.service.impl;

import com.foxapplication.glhmcloud.dao.KeyValueDao;
import com.foxapplication.glhmcloud.entity.KeyValueEntity;
import com.foxapplication.glhmcloud.service.KeyValueDatabaseService;
import org.dromara.hutool.core.convert.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeyValueDatabaseServiceImpl implements KeyValueDatabaseService {

    private final KeyValueDao keyValueDao;

    @Autowired
    public KeyValueDatabaseServiceImpl(KeyValueDao keyValueDao) {
        this.keyValueDao = keyValueDao;
    }

    @Override
    @Transactional
    public void put(String key, Object value) {
        String valueStr = Converter.identity().convert(String.class, value);
        keyValueDao.saveAndFlush(new KeyValueEntity(key, valueStr));
    }

    /**
     * 获取Object属性值
     *
     * @param key          属性名
     * @param defaultValue 默认值
     * @return 属性值，无对应值返回defaultValue
     */
    @Override
    @Transactional
    public Object getObj(String key, Object defaultValue) {
        if (keyValueDao.findById(key).isPresent()) {
            return keyValueDao.findById(key).get().getValue();
        }
        return defaultValue;
    }
}
