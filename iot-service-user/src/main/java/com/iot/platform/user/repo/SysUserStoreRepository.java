package com.iot.platform.user.repo;

import com.iot.platform.user.domain.SysUserStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SysUserStoreRepository extends JpaRepository<SysUserStore, Long> {

    List<SysUserStore> findByUser_Id(Long userId);

    boolean existsByUser_IdAndStore_Id(Long userId, Long storeId);
}
