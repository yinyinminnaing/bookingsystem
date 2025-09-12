package com.example.bookingsystem.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisWaitlistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WAITLIST_PREFIX = "waitlist:";
    private static final long WAITLIST_TTL = 24; // hours

    public void addToWaitlist(Integer classId, Integer userId) {
        String key = WAITLIST_PREFIX + classId;
        redisTemplate.opsForList().rightPush(key, userId);
        redisTemplate.expire(key, WAITLIST_TTL, TimeUnit.HOURS);
    }

    public Integer getNextWaitlistUser(Integer classId) {
        String key = WAITLIST_PREFIX + classId;
        return (Integer) redisTemplate.opsForList().leftPop(key);
    }

    public List<Integer> getWaitlist(Integer classId) {
        String key = WAITLIST_PREFIX + classId;
        List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);
        if (rawList == null) {
            return null;
        }

        return rawList.stream()
                .map(obj -> (Integer) obj)
                .toList();
    }

    public Long getWaitlistPosition(Integer classId, Integer userId) {
        String key = WAITLIST_PREFIX + classId;
        List<Integer> waitlist = getWaitlist(classId);
        if (waitlist != null) {
            for (int i = 0; i < waitlist.size(); i++) {
                if (waitlist.get(i).equals(userId)) {
                    return (long) (i + 1);
                }
            }
        }
        return null;
    }

    public void removeFromWaitlist(Integer classId, Integer userId) {
        String key = WAITLIST_PREFIX + classId;
        redisTemplate.opsForList().remove(key, 0, userId);
    }

    public Long getWaitlistSize(Integer classId) {
        String key = WAITLIST_PREFIX + classId;
        return redisTemplate.opsForList().size(key);
    }
}