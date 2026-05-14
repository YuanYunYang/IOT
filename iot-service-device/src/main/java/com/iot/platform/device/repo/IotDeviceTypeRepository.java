package com.iot.platform.device.repo;

import com.iot.platform.device.domain.IotDeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IotDeviceTypeRepository extends JpaRepository<IotDeviceType, Long> {
    boolean existsByTypeCode(String typeCode);
    Optional<IotDeviceType> findByTypeCode(String typeCode);
}

