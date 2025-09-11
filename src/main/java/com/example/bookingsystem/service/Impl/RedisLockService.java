package com.example.bookingsystem.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOCK_PREFIX = "lock:";
    private static final long LOCK_TIMEOUT = 10; // seconds

    public boolean acquireLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "locked", LOCK_TIMEOUT, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(acquired);
    }

    public void releaseLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        redisTemplate.delete(key);
    }

    public boolean executeWithLock(String lockKey, Runnable task) {
        if (acquireLock(lockKey)) {
            try {
                task.run();
                return true;
            } finally {
                releaseLock(lockKey);
            }
        }
        return false;
    }
}
