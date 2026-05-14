package com.iot.platform.user.service;

import com.iot.platform.user.domain.SysPermission;
import com.iot.platform.user.domain.SysRole;
import com.iot.platform.user.dto.RoleRequest;
import com.iot.platform.user.dto.RoleResponse;
import com.iot.platform.user.repo.SysPermissionRepository;
import com.iot.platform.user.repo.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAdminService {

    private final SysRoleRepository roleRepository;
    private final SysPermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> listAll() {
        return roleRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse get(Long id) {
        return roleRepository.findById(id).map(this::toResponse).orElseThrow(() -> new IllegalArgumentException("角色不存在"));
    }

    @Transactional
    public RoleResponse create(RoleRequest req) {
        if (roleRepository.existsByRoleCode(req.getRoleCode())) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        SysRole r = new SysRole();
        r.setRoleCode(req.getRoleCode());
        r.setRoleName(req.getRoleName());
        r.setRemark(req.getRemark());
        applyPermissions(r, req.getPermissionIds());
        roleRepository.save(r);
        return toResponse(r);
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest req) {
        SysRole r = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        if (!r.getRoleCode().equals(req.getRoleCode()) && roleRepository.existsByRoleCode(req.getRoleCode())) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        r.setRoleCode(req.getRoleCode());
        r.setRoleName(req.getRoleName());
        r.setRemark(req.getRemark());
        if (req.getPermissionIds() != null) {
            applyPermissions(r, req.getPermissionIds());
        }
        roleRepository.save(r);
        return toResponse(r);
    }

    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    private void applyPermissions(SysRole r, List<Long> permissionIds) {
        r.getPermissions().clear();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        Set<SysPermission> perms = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (perms.size() != permissionIds.size()) {
            throw new IllegalArgumentException("部分权限 id 不存在");
        }
        r.getPermissions().addAll(perms);
    }

    private RoleResponse toResponse(SysRole r) {
        List<Long> pids = r.getPermissions().stream().map(SysPermission::getId).collect(Collectors.toList());
        return RoleResponse.builder()
                .id(r.getId())
                .roleCode(r.getRoleCode())
                .roleName(r.getRoleName())
                .remark(r.getRemark())
                .createdAt(r.getCreatedAt())
                .permissionIds(pids)
                .build();
    }
}
