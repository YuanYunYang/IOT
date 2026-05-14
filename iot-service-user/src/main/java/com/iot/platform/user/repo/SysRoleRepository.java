package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);
}
