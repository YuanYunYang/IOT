package com.iot.platform.user.service;

import com.iot.platform.user.domain.SysStore;
import com.iot.platform.user.domain.SysTenant;
import com.iot.platform.user.domain.TenantType;
import com.iot.platform.user.dto.StoreBrief;
import com.iot.platform.user.dto.StoreCreateRequest;
import com.iot.platform.user.repo.SysStoreRepository;
import com.iot.platform.user.repo.SysTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreAdminService {

    private final SysTenantRepository tenantRepository;
    private final SysStoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<StoreBrief> listByTenant(Long tenantId) {
        return storeRepository.findByTenant_IdOrderBySortOrderAscIdAsc(tenantId).stream()
                .map(s -> StoreBrief.builder()
                        .id(s.getId())
                        .storeCode(s.getStoreCode())
                        .storeName(s.getStoreName())
                        .enabled(s.getEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreBrief create(Long tenantId, StoreCreateRequest req) {
        SysTenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        if (tenant.getTenantType() == TenantType.SINGLE_STORE) {
            long count = storeRepository.findByTenant_IdOrderBySortOrderAscIdAsc(tenantId).size();
            if (count >= 1) {
                throw new IllegalArgumentException("单门店租户仅允许创建一个门店");
            }
        }
        SysStore s = new SysStore();
        s.setTenant(tenant);
        s.setStoreCode(req.getStoreCode().trim());
        s.setStoreName(req.getStoreName().trim());
        s.setEnabled(true);
        s.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        storeRepository.save(s);
        return StoreBrief.builder()
                .id(s.getId())
                .storeCode(s.getStoreCode())
                .storeName(s.getStoreName())
                .enabled(s.getEnabled())
                .build();
    }
}
