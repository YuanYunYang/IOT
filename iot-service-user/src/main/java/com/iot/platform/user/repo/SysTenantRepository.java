package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysTenantRepository extends JpaRepository<SysTenant, Long> {

    Optional<SysTenant> findByTenantCode(String tenantCode);

    boolean existsByTenantCode(String tenantCode);
}
