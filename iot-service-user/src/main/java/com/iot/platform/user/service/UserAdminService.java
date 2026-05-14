package com.iot.platform.user.service;

import com.iot.platform.user.domain.SysRole;
import com.iot.platform.user.domain.SysStore;
import com.iot.platform.user.domain.SysTenant;
import com.iot.platform.user.domain.SysUser;
import com.iot.platform.user.domain.SysUserStore;
import com.iot.platform.user.domain.TenantType;
import com.iot.platform.user.dto.UserCreateRequest;
import com.iot.platform.user.dto.UserResponse;
import com.iot.platform.user.dto.UserUpdateRequest;
import com.iot.platform.user.repo.SysRoleRepository;
import com.iot.platform.user.repo.SysStoreRepository;
import com.iot.platform.user.repo.SysTenantRepository;
import com.iot.platform.user.repo.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysTenantRepository tenantRepository;
    private final SysStoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        SysUser u = userRepository.loadWithTenantAndStores(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return toResponse(u);
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        SysTenant tenant = tenantRepository.findById(req.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        if (!Boolean.TRUE.equals(tenant.getEnabled())) {
            throw new IllegalArgumentException("租户已停用");
        }
        if (tenant.getTenantType() == TenantType.SINGLE_STORE && req.getAccessibleStoreIds().size() != 1) {
            throw new IllegalArgumentException("单门店租户用户仅能绑定一个门店");
        }
        if (userRepository.existsByTenant_IdAndUsername(req.getTenantId(), req.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        SysUser u = new SysUser();
        u.setTenant(tenant);
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRealName(req.getRealName());
        u.setEnabled(req.getEnabled() != null ? req.getEnabled() : true);
        applyRoles(u, req.getRoleIds());
        applyStoreAssignments(u, tenant, req.getAccessibleStoreIds());
        applyDefaultStore(u, tenant, req.getDefaultStoreId(), req.getAccessibleStoreIds());
        userRepository.save(u);
        return toResponse(userRepository.loadWithTenantAndStores(u.getId()).orElse(u));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest req) {
        SysUser u = userRepository.loadWithTenantAndStores(id).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        SysTenant tenant = u.getTenant();
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getRealName() != null) {
            u.setRealName(req.getRealName());
        }
        if (req.getEnabled() != null) {
            u.setEnabled(req.getEnabled());
        }
        if (req.getRoleIds() != null) {
            applyRoles(u, req.getRoleIds());
        }
        if (req.getAccessibleStoreIds() != null) {
            if (req.getAccessibleStoreIds().isEmpty()) {
                throw new IllegalArgumentException("可访问门店不能为空");
            }
            if (tenant.getTenantType() == TenantType.SINGLE_STORE && req.getAccessibleStoreIds().size() != 1) {
                throw new IllegalArgumentException("单门店租户用户仅能绑定一个门店");
            }
            applyStoreAssignments(u, tenant, req.getAccessibleStoreIds());
            ensureDefaultStillValid(u);
        }
        if (req.getDefaultStoreId() != null) {
            SysStore ds = storeRepository.findByIdAndTenant_Id(req.getDefaultStoreId(), tenant.getId())
                    .orElseThrow(() -> new IllegalArgumentException("默认门店不存在或不属于该租户"));
            if (!userHasStoreAssignment(u, req.getDefaultStoreId())) {
                throw new IllegalArgumentException("默认门店须在可访问门店列表中");
            }
            u.setDefaultStore(ds);
        }
        u.setUpdatedAt(Instant.now());
        userRepository.save(u);
        return toResponse(userRepository.loadWithTenantAndStores(id).orElse(u));
    }

    private boolean userHasStoreAssignment(SysUser u, Long storeId) {
        return u.getStoreAssignments().stream().anyMatch(a -> a.getStore().getId().equals(storeId));
    }

    /** 变更门店绑定后，若原默认门店不在新列表中则回退为排序第一的门店 */
    private void ensureDefaultStillValid(SysUser u) {
        if (u.getStoreAssignments().isEmpty()) {
            u.setDefaultStore(null);
            return;
        }
        if (u.getDefaultStore() != null && userHasStoreAssignment(u, u.getDefaultStore().getId())) {
            return;
        }
        SysStore first = u.getStoreAssignments().stream()
                .map(SysUserStore::getStore)
                .min(Comparator.comparing(SysStore::getSortOrder).thenComparing(SysStore::getId))
                .orElse(null);
        u.setDefaultStore(first);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void applyRoles(SysUser u, List<Long> roleIds) {
        u.getRoles().clear();
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        Set<SysRole> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色 id 不存在");
        }
        u.getRoles().addAll(roles);
    }

    private void applyStoreAssignments(SysUser u, SysTenant tenant, List<Long> storeIds) {
        u.getStoreAssignments().clear();
        if (storeIds == null || storeIds.isEmpty()) {
            return;
        }
        for (Long sid : storeIds) {
            SysStore store = storeRepository.findByIdAndTenant_Id(sid, tenant.getId())
                    .orElseThrow(() -> new IllegalArgumentException("门店不存在或不属于该租户: " + sid));
            if (!Boolean.TRUE.equals(store.getEnabled())) {
                throw new IllegalArgumentException("门店已停用: " + sid);
            }
            SysUserStore row = new SysUserStore();
            row.setUser(u);
            row.setStore(store);
            u.getStoreAssignments().add(row);
        }
    }

    private void applyDefaultStore(SysUser u, SysTenant tenant, Long defaultStoreId, List<Long> accessibleStoreIds) {
        if (accessibleStoreIds == null || accessibleStoreIds.isEmpty()) {
            u.setDefaultStore(null);
            return;
        }
        if (defaultStoreId != null) {
            if (!accessibleStoreIds.contains(defaultStoreId)) {
                throw new IllegalArgumentException("默认门店须在可访问门店列表中");
            }
            SysStore ds = storeRepository.findByIdAndTenant_Id(defaultStoreId, tenant.getId())
                    .orElseThrow(() -> new IllegalArgumentException("默认门店不存在"));
            u.setDefaultStore(ds);
            return;
        }
        SysStore first = u.getStoreAssignments().stream()
                .map(SysUserStore::getStore)
                .min(Comparator.comparing(SysStore::getSortOrder).thenComparing(SysStore::getId))
                .orElse(null);
        u.setDefaultStore(first);
    }

    private UserResponse toResponse(SysUser u) {
        List<String> codes = u.getRoles().stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        List<Long> storeIds = u.getStoreAssignments().stream()
                .map(a -> a.getStore().getId())
                .collect(Collectors.toList());
        SysTenant t = u.getTenant();
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .realName(u.getRealName())
                .enabled(u.getEnabled())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .roleCodes(codes)
                .tenantId(t != null ? t.getId() : null)
                .tenantCode(t != null ? t.getTenantCode() : null)
                .tenantType(t != null ? t.getTenantType().name() : null)
                .defaultStoreId(u.getDefaultStore() != null ? u.getDefaultStore().getId() : null)
                .accessibleStoreIds(storeIds)
                .build();
    }
}
