package com.iot.platform.alarm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.alarm.config.IotAlarmProperties;
import com.iot.platform.alarm.dto.DevicePointMeta;
import com.iot.platform.alarm.dto.DeviceTypeMeta;
import com.iot.platform.starter.redis.IotRedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceMetaRedisReader {

    private final IotRedisHelper redis;
    private final ObjectMapper objectMapper;
    private final IotAlarmProperties props;

    public Optional<DevicePointMeta> getPoint(Long pointId) {
        if (pointId == null) {
            return Optional.empty();
        }
        String key = normalizePrefix(props.getDeviceMetaKeyPrefix()) + "point:" + pointId;
        Optional<String> raw = redis.get(key);
        if (!raw.isPresent() || raw.get() == null || raw.get().trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw.get(), DevicePointMeta.class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse device point meta from redis key=" + key, e);
        }
    }

    public Optional<DeviceTypeMeta> getType(Long typeId) {
        if (typeId == null) {
            return Optional.empty();
        }
        String key = normalizePrefix(props.getDeviceMetaKeyPrefix()) + "type:" + typeId;
        Optional<String> raw = redis.get(key);
        if (!raw.isPresent() || raw.get() == null || raw.get().trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw.get(), DeviceTypeMeta.class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse device type meta from redis key=" + key, e);
        }
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return "iot:device:meta:";
        }
        return prefix.trim();
    }
}

