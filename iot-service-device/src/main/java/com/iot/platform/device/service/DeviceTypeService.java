package com.iot.platform.device.service;

import com.iot.platform.device.domain.IotDeviceType;
import com.iot.platform.device.cache.DeviceMetadataCacheService;
import com.iot.platform.device.domain.IotDevicePoint;
import com.iot.platform.device.dto.DeviceTypeRequest;
import com.iot.platform.device.repo.IotDevicePointRepository;
import com.iot.platform.device.repo.IotDeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTypeService {

    private final IotDeviceTypeRepository typeRepository;
    private final IotDevicePointRepository pointRepository;
    private final DeviceMetadataCacheService cacheService;

    @Transactional(readOnly = true)
    public List<IotDeviceType> listAll() {
        return typeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public IotDeviceType get(Long id) {
        return typeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备类型不存在"));
    }

    @Transactional
    public IotDeviceType create(DeviceTypeRequest req) {
        if (typeRepository.existsByTypeCode(req.getTypeCode())) {
            throw new IllegalArgumentException("设备类型编码已存在");
        }
        IotDeviceType t = map(new IotDeviceType(), req);
        IotDeviceType saved = typeRepository.save(t);
        cacheService.refreshTypesAfterCommit();
        return saved;
    }

    @Transactional
    public IotDeviceType update(Long id, DeviceTypeRequest req) {
        IotDeviceType t = typeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备类型不存在"));
        if (!t.getTypeCode().equals(req.getTypeCode()) && typeRepository.existsByTypeCode(req.getTypeCode())) {
            throw new IllegalArgumentException("设备类型编码已存在");
        }
        map(t, req);
        t.setUpdatedAt(Instant.now());
        IotDeviceType saved = typeRepository.save(t);
        cacheService.refreshTypesAfterCommit();
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        // 先删点位；库表级联亦可，但需先拿到 id 以清理缓存
        List<IotDevicePoint> points = pointRepository.findByDeviceTypeIdOrderBySortOrderAscIdAsc(id);
        pointRepository.deleteAll(points);
        typeRepository.deleteById(id);
        cacheService.evictTypeAndPointsAfterCommit(id);
    }

    private IotDeviceType map(IotDeviceType t, DeviceTypeRequest req) {
        t.setTypeCode(req.getTypeCode());
        t.setTypeName(req.getTypeName());
        t.setSubsystem(req.getSubsystem());
        t.setRemark(req.getRemark());
        return t;
    }
}

