package com.example.javabasics.redislock.code;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RedisLock {
    @Resource
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     *
     * @param key   锁号
     * @param value 当前时间+超时时间
     * @return 锁是否成功
     */
    public boolean lock(String key, String value) {
        //SETNX命令, 可以设置返回true, 不可以返回false
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (aBoolean != null && aBoolean) {
            return true;
        }

        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁过期
        if (StringUtils.isNotEmpty(currentValue) && (Long.parseLong(currentValue) < System.currentTimeMillis())) {
            //GETSET命令, 获取上一个锁的时间
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            return StringUtils.isNotEmpty(oldValue) && oldValue.equals(value);
        }
        return false;
    }

    /**
     * 解锁
     */
    public void unLock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
