package com.iot.platform.device.service;

import com.iot.platform.device.domain.IotDevicePoint;
import com.iot.platform.device.cache.DeviceMetadataCacheService;
import com.iot.platform.device.dto.DevicePointRequest;
import com.iot.platform.device.repo.IotDevicePointRepository;
import com.iot.platform.device.repo.IotDeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DevicePointService {

    private final IotDevicePointRepository pointRepository;
    private final IotDeviceTypeRepository typeRepository;
    private final DeviceMetadataCacheService cacheService;

    @Transactional(readOnly = true)
    public List<IotDevicePoint> listByType(Long deviceTypeId) {
        return pointRepository.findByDeviceTypeIdOrderBySortOrderAscIdAsc(deviceTypeId);
    }

    @Transactional(readOnly = true)
    public IotDevicePoint get(Long id) {
        return pointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("点位不存在"));
    }

    @Transactional
    public IotDevicePoint create(DevicePointRequest req) {
        if (!typeRepository.existsById(req.getDeviceTypeId())) {
            throw new IllegalArgumentException("设备类型不存在");
        }
        if (pointRepository.existsByDeviceTypeIdAndPointCode(req.getDeviceTypeId(), req.getPointCode())) {
            throw new IllegalArgumentException("点位编码已存在（同类型内唯一）");
        }
        IotDevicePoint p = map(new IotDevicePoint(), req);
        IotDevicePoint saved = pointRepository.save(p);
        cacheService.refreshPointsAfterCommit(saved.getDeviceTypeId());
        return saved;
    }

    @Transactional
    public IotDevicePoint update(Long id, DevicePointRequest req) {
        IotDevicePoint p = pointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("点位不存在"));
        Long oldTypeId = p.getDeviceTypeId();
        if (!p.getDeviceTypeId().equals(req.getDeviceTypeId())) {
            // 允许改类型，但要做存在性与唯一性校验
            if (!typeRepository.existsById(req.getDeviceTypeId())) {
                throw new IllegalArgumentException("设备类型不存在");
            }
        }
        if ((!p.getDeviceTypeId().equals(req.getDeviceTypeId()) || !p.getPointCode().equals(req.getPointCode()))
                && pointRepository.existsByDeviceTypeIdAndPointCode(req.getDeviceTypeId(), req.getPointCode())) {
            throw new IllegalArgumentException("点位编码已存在（同类型内唯一）");
        }
        map(p, req);
        p.setUpdatedAt(Instant.now());
        IotDevicePoint saved = pointRepository.save(p);
        cacheService.refreshPointsAfterCommit(oldTypeId);
        cacheService.refreshPointsAfterCommit(saved.getDeviceTypeId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        IotDevicePoint p = pointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("点位不存在"));
        pointRepository.deleteById(id);
        cacheService.evictPointAfterCommit(id, p.getDeviceTypeId());
    }

    private IotDevicePoint map(IotDevicePoint p, DevicePointRequest req) {
        p.setDeviceTypeId(req.getDeviceTypeId());
        p.setPointCode(req.getPointCode());
        p.setPointName(req.getPointName());
        p.setUnit(req.getUnit());
        p.setValueType(req.getValueType());
        p.setExternalPointId(req.getExternalPointId());
        p.setEnabled(req.getEnabled());
        p.setSortOrder(req.getSortOrder());
        p.setRemark(req.getRemark());
        return p;
    }
}

