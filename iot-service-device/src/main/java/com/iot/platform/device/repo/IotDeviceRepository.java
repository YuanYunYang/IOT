package com.iot.platform.device.repo;

import com.iot.platform.device.domain.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IotDeviceRepository extends JpaRepository<IotDevice, Long> {

    List<IotDevice> findBySubsystemOrderByIdDesc(String subsystem);

    List<IotDevice> findByDeviceTypeIdOrderByIdDesc(Long deviceTypeId);
}
