package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {

    Optional<SysPermission> findByPermCode(String permCode);

    boolean existsByPermCode(String permCode);

    List<SysPermission> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<SysPermission> findAllByOrderBySortOrderAsc();
}
