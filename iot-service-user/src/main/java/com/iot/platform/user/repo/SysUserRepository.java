package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    @Query("select distinct u from SysUser u "
            + "join fetch u.tenant t "
            + "left join fetch u.storeAssignments sa "
            + "left join fetch sa.store s "
            + "left join fetch u.defaultStore ds "
            + "where t.id = :tenantId and u.username = :username")
    Optional<SysUser> loadForLogin(@Param("tenantId") Long tenantId, @Param("username") String username);

    @Query("select distinct u from SysUser u "
            + "join fetch u.tenant t "
            + "left join fetch u.storeAssignments sa "
            + "left join fetch sa.store s "
            + "left join fetch u.defaultStore ds "
            + "where u.id = :id")
    Optional<SysUser> loadWithTenantAndStores(@Param("id") Long id);

    Optional<SysUser> findByTenant_IdAndUsername(Long tenantId, String username);

    boolean existsByTenant_IdAndUsername(Long tenantId, String username);
}
