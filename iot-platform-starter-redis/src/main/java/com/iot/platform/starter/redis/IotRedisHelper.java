package com.iot.platform.starter.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 面向字符串的 Redis 辅助方法：单键/批量读写、哈希、删除、过期等。
 */
public class IotRedisHelper {

    private final StringRedisTemplate redis;

    public IotRedisHelper(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redis.opsForValue().get(key));
    }

    public List<String> multiGet(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> raw = redis.opsForValue().multiGet(keys);
        if (raw == null) {
            return new ArrayList<>();
        }
        return raw;
    }

    public void set(String key, String value) {
        redis.opsForValue().set(key, value);
    }

    public void set(String key, String value, long ttlSeconds) {
        redis.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public void set(String key, String value, Duration ttl) {
        redis.opsForValue().set(key, value, ttl);
    }

    public void multiSet(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        redis.opsForValue().multiSet(map);
    }

    public Boolean delete(String key) {
        return redis.delete(key);
    }

    public Long delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        return redis.delete(keys);
    }

    public Boolean hasKey(String key) {
        return redis.hasKey(key);
    }

    public Boolean expire(String key, long ttlSeconds) {
        return redis.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    public Long increment(String key) {
        return redis.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return redis.opsForValue().increment(key, delta);
    }

    public Optional<String> hGet(String key, String field) {
        return Optional.ofNullable(redis.opsForHash().get(key, field))
                .map(Object::toString);
    }

    public Map<String, String> hGetAll(String key) {
        Map<Object, Object> raw = redis.opsForHash().entries(key);
        Map<String, String> out = new HashMap<>();
        raw.forEach((k, v) -> out.put(String.valueOf(k), v == null ? null : String.valueOf(v)));
        return out;
    }

    public void hSet(String key, String field, String value) {
        redis.opsForHash().put(key, field, value);
    }

    public void hMSet(String key, Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        redis.opsForHash().putAll(key, new HashMap<>(map));
    }

    public Long hDelete(String key, Object... fields) {
        return redis.opsForHash().delete(key, fields);
    }
}
