package com.iot.platform.user.service;

import com.iot.platform.user.domain.SysTenant;
import com.iot.platform.user.dto.TenantCreateRequest;
import com.iot.platform.user.dto.TenantResponse;
import com.iot.platform.user.repo.SysTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantAdminService {

    private final SysTenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public List<TenantResponse> listAll() {
        return tenantRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public TenantResponse create(TenantCreateRequest req) {
        if (tenantRepository.existsByTenantCode(req.getTenantCode().trim())) {
            throw new IllegalArgumentException("租户编码已存在");
        }
        SysTenant t = new SysTenant();
        t.setTenantCode(req.getTenantCode().trim());
        t.setTenantName(req.getTenantName().trim());
        t.setTenantType(req.getTenantType());
        t.setEnabled(true);
        tenantRepository.save(t);
        return toResponse(t);
    }

    private TenantResponse toResponse(SysTenant t) {
        return TenantResponse.builder()
                .id(t.getId())
                .tenantCode(t.getTenantCode())
                .tenantName(t.getTenantName())
                .tenantType(t.getTenantType().name())
                .enabled(t.getEnabled())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
