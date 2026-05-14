package com.iot.platform.user.service;

import com.iot.platform.user.domain.SysPermission;
import com.iot.platform.user.dto.PermissionRequest;
import com.iot.platform.user.dto.PermissionResponse;
import com.iot.platform.user.repo.SysPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionAdminService {

    private final SysPermissionRepository permissionRepository;

    public List<PermissionResponse> listFlat() {
        return permissionRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponseFlat)
                .collect(Collectors.toList());
    }

    public List<PermissionResponse> tree() {
        List<SysPermission> all = permissionRepository.findAllByOrderBySortOrderAsc();
        Map<Long, PermissionResponse> nodes = all.stream()
                .collect(Collectors.toMap(SysPermission::getId, this::toResponseFlat));
        List<PermissionResponse> roots = new ArrayList<>();
        for (SysPermission p : all) {
            PermissionResponse n = nodes.get(p.getId());
            if (p.getParentId() == null) {
                roots.add(n);
            } else {
                PermissionResponse parent = nodes.get(p.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }
        return roots;
    }

    public PermissionResponse get(Long id) {
        return permissionRepository.findById(id).map(this::toResponseFlat)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在"));
    }

    @Transactional
    public PermissionResponse create(PermissionRequest req) {
        if (permissionRepository.existsByPermCode(req.getPermCode())) {
            throw new IllegalArgumentException("权限编码已存在");
        }
        SysPermission e = map(req);
        permissionRepository.save(e);
        return toResponseFlat(e);
    }

    @Transactional
    public PermissionResponse update(Long id, PermissionRequest req) {
        SysPermission e = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("权限不存在"));
        if (!e.getPermCode().equals(req.getPermCode()) && permissionRepository.existsByPermCode(req.getPermCode())) {
            throw new IllegalArgumentException("权限编码已存在");
        }
        e.setParentId(req.getParentId());
        e.setPermType(req.getPermType());
        e.setPermCode(req.getPermCode());
        e.setName(req.getName());
        e.setPath(req.getPath());
        e.setHttpMethod(req.getHttpMethod());
        e.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        permissionRepository.save(e);
        return toResponseFlat(e);
    }

    @Transactional
    public void delete(Long id) {
        // 看产品设计，如果确认要删除当前权限，则子权限也全部删除。
//        List<SysPermission> children = permissionRepository.findByParentIdOrderBySortOrderAsc(id);
//        if (!children.isEmpty()) {
//            throw new IllegalArgumentException("请先删除子权限");
//        }
        permissionRepository.deleteById(id);
    }

    private SysPermission map(PermissionRequest req) {
        SysPermission e = new SysPermission();
        e.setParentId(req.getParentId());
        e.setPermType(req.getPermType());
        e.setPermCode(req.getPermCode());
        e.setName(req.getName());
        e.setPath(req.getPath());
        e.setHttpMethod(req.getHttpMethod());
        e.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        return e;
    }

    private PermissionResponse toResponseFlat(SysPermission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .parentId(p.getParentId())
                .permType(p.getPermType())
                .permCode(p.getPermCode())
                .name(p.getName())
                .path(p.getPath())
                .httpMethod(p.getHttpMethod())
                .sortOrder(p.getSortOrder())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
