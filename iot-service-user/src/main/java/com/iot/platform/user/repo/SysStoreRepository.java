package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysStoreRepository extends JpaRepository<SysStore, Long> {

    List<SysStore> findByTenant_IdOrderBySortOrderAscIdAsc(Long tenantId);

    Optional<SysStore> findByIdAndTenant_Id(Long id, Long tenantId);
}
