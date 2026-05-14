package com.iot.platform.device.service;

import com.iot.platform.device.domain.IotDevice;
import com.iot.platform.device.domain.IotDeviceType;
import com.iot.platform.device.dto.DeviceRequest;
import com.iot.platform.device.dto.DeviceResponse;
import com.iot.platform.device.repo.IotDeviceRepository;
import com.iot.platform.device.repo.IotDeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final IotDeviceRepository deviceRepository;
    private final IotDeviceTypeRepository typeRepository;

    @Transactional(readOnly = true)
    public List<DeviceResponse> listAll() {
        return toResponses(deviceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public DeviceResponse get(Long id) {
        IotDevice d = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        return toResponse(d, loadTypeNameMap(Collections.singletonList(d)));
    }

    @Transactional
    public DeviceResponse create(DeviceRequest req) {
        IotDevice d = map(new IotDevice(), req);
        IotDevice saved = deviceRepository.save(d);
        return toResponse(saved, loadTypeNameMap(Collections.singletonList(saved)));
    }

    @Transactional
    public DeviceResponse update(Long id, DeviceRequest req) {
        IotDevice d = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        map(d, req);
        d.setUpdatedAt(Instant.now());
        IotDevice saved = deviceRepository.save(d);
        return toResponse(saved, loadTypeNameMap(Collections.singletonList(saved)));
    }

    @Transactional
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    private IotDevice map(IotDevice d, DeviceRequest req) {
        d.setSubsystem(req.getSubsystem());
        d.setDeviceTypeId(req.getDeviceTypeId());
        validateDeviceTypeIfPresent(req.getDeviceTypeId());
        d.setDeviceName(req.getDeviceName());
        d.setLocation(req.getLocation());
        d.setExternalDeviceId(req.getExternalDeviceId());
        d.setRemark(req.getRemark());
        return d;
    }

    private void validateDeviceTypeIfPresent(Long typeId) {
        if (typeId == null) {
            return;
        }
        Optional<IotDeviceType> t = typeRepository.findById(typeId);
        if (!t.isPresent()) {
            throw new IllegalArgumentException("设备类型不存在");
        }
    }

    private List<DeviceResponse> toResponses(List<IotDevice> devices) {
        if (devices == null || devices.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> typeNameMap = loadTypeNameMap(devices);
        return devices.stream().map(d -> toResponse(d, typeNameMap)).collect(Collectors.toList());
    }

    private Map<Long, String> loadTypeNameMap(List<IotDevice> devices) {
        if (devices == null || devices.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> typeIds = devices.stream()
                .map(IotDevice::getDeviceTypeId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (typeIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> map = new HashMap<>();
        for (IotDeviceType t : typeRepository.findAllById(typeIds)) {
            map.put(t.getId(), t.getTypeName());
        }
        return map;
    }

    private DeviceResponse toResponse(IotDevice d, Map<Long, String> typeNameMap) {
        DeviceResponse r = new DeviceResponse();
        r.setId(d.getId());
        r.setSubsystem(d.getSubsystem());
        r.setDeviceTypeId(d.getDeviceTypeId());
        String typeName = d.getDeviceTypeId() == null ? null : typeNameMap.get(d.getDeviceTypeId());
        r.setDeviceTypeName(typeName);
        r.setDeviceName(d.getDeviceName());
        r.setLocation(d.getLocation());
        r.setExternalDeviceId(d.getExternalDeviceId());
        r.setRemark(d.getRemark());
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());
        return r;
    }
}
