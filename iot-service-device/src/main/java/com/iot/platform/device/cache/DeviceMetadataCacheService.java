package com.iot.platform.device.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.device.domain.IotDevicePoint;
import com.iot.platform.device.domain.IotDeviceType;
import com.iot.platform.device.repo.IotDevicePointRepository;
import com.iot.platform.device.repo.IotDeviceTypeRepository;
import com.iot.platform.starter.redis.IotRedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "iot.device.metadata-cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DeviceMetadataCacheService {

    private final IotRedisHelper redis;
    private final ObjectMapper objectMapper;
    private final DeviceMetadataCacheProperties props;

    private final IotDeviceTypeRepository typeRepository;
    private final IotDevicePointRepository pointRepository;

    public void refreshAllAfterCommit() {
        afterCommit(this::refreshAll);
    }

    public void refreshTypesAfterCommit() {
        afterCommit(this::refreshTypes);
    }

    public void refreshPointsAfterCommit(Long deviceTypeId) {
        if (deviceTypeId == null) {
            return;
        }
        afterCommit(() -> refreshPoints(deviceTypeId));
    }

    public void refreshAll() {
        refreshTypes();
        List<IotDeviceType> types = typeRepository.findAll();
        for (IotDeviceType t : types) {
            refreshPoints(t.getId());
        }
    }

    public void refreshTypes() {
        List<IotDeviceType> types = typeRepository.findAll();
        String prefix = normalizePrefix(props.getKeyPrefix());
        redis.set(DeviceMetadataCacheKeys.typeList(prefix), toJson(types));
        for (IotDeviceType t : types) {
            redis.set(DeviceMetadataCacheKeys.type(prefix, t.getId()), toJson(t));
        }
    }

    public void refreshPoints(Long deviceTypeId) {
        List<IotDevicePoint> points = pointRepository.findByDeviceTypeIdOrderBySortOrderAscIdAsc(deviceTypeId);
        String prefix = normalizePrefix(props.getKeyPrefix());
        redis.set(DeviceMetadataCacheKeys.pointListByType(prefix, deviceTypeId), toJson(points));
        for (IotDevicePoint p : points) {
            redis.set(DeviceMetadataCacheKeys.point(prefix, p.getId()), toJson(p));
        }
    }

    public void evictTypeAndPointsAfterCommit(Long deviceTypeId) {
        if (deviceTypeId == null) {
            return;
        }
        afterCommit(() -> evictTypeAndPoints(deviceTypeId));
    }

    public void evictPointAfterCommit(Long pointId, Long deviceTypeId) {
        afterCommit(() -> {
            String prefix = normalizePrefix(props.getKeyPrefix());
            if (pointId != null) {
                redis.delete(DeviceMetadataCacheKeys.point(prefix, pointId));
            }
            if (deviceTypeId != null) {
                refreshPoints(deviceTypeId);
            }
        });
    }

    private void evictTypeAndPoints(Long deviceTypeId) {
        String prefix = normalizePrefix(props.getKeyPrefix());
        redis.delete(DeviceMetadataCacheKeys.type(prefix, deviceTypeId));
        redis.delete(DeviceMetadataCacheKeys.pointListByType(prefix, deviceTypeId));
        // 按库中快照尽力删除各 point:{id} 键
        List<IotDevicePoint> points = pointRepository.findByDeviceTypeIdOrderBySortOrderAscIdAsc(deviceTypeId);
        for (IotDevicePoint p : points) {
            redis.delete(DeviceMetadataCacheKeys.point(prefix, p.getId()));
        }
        refreshTypes();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpOnStart() {
        refreshAll();
    }

    private void afterCommit(Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis metadata JSON serialize failed", e);
        }
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return "iot:device:meta:";
        }
        return prefix.trim();
    }
}

