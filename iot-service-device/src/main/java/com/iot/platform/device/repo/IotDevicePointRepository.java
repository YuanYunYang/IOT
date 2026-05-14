package com.iot.platform.device.repo;

import com.iot.platform.device.domain.IotDevicePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IotDevicePointRepository extends JpaRepository<IotDevicePoint, Long> {
    List<IotDevicePoint> findByDeviceTypeIdOrderBySortOrderAscIdAsc(Long deviceTypeId);
    Optional<IotDevicePoint> findByDeviceTypeIdAndPointCode(Long deviceTypeId, String pointCode);
    boolean existsByDeviceTypeIdAndPointCode(Long deviceTypeId, String pointCode);
}

