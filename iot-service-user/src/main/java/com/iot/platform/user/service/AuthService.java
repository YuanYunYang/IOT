package com.iot.platform.user.service;

import com.iot.platform.common.security.jwt.JwtClaimKeys;
import com.iot.platform.common.security.jwt.JwtCodec;
import com.iot.platform.common.security.jwt.JwtSecurityProperties;
import com.iot.platform.user.domain.SysStore;
import com.iot.platform.user.domain.SysTenant;
import com.iot.platform.user.domain.SysUser;
import com.iot.platform.user.domain.SysUserStore;
import com.iot.platform.user.domain.TenantType;
import com.iot.platform.user.dto.AuthProfile;
import com.iot.platform.user.dto.DefaultStoreRequest;
import com.iot.platform.user.dto.LoginRequest;
import com.iot.platform.user.dto.LoginResponse;
import com.iot.platform.user.dto.StoreBrief;
import com.iot.platform.user.repo.SysTenantRepository;
import com.iot.platform.user.repo.SysUserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository userRepository;
    private final SysTenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtSecurityProperties jwtSecurityProperties;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        if (!StringUtils.hasText(jwtSecurityProperties.getSecret())) {
            throw new IllegalStateException("未配置 iot.security.jwt.secret，无法签发令牌");
        }
        SysTenant tenant = tenantRepository.findByTenantCode(req.getTenantCode().trim())
                .orElseThrow(() -> new IllegalArgumentException("租户不存在或编码错误"));
        if (!Boolean.TRUE.equals(tenant.getEnabled())) {
            throw new IllegalArgumentException("租户已停用");
        }
        SysUser u = userRepository.loadForLogin(tenant.getId(), req.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!Boolean.TRUE.equals(u.getEnabled())) {
            throw new IllegalArgumentException("账号已禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        Long currentStoreId = resolveCurrentStoreId(u);
        return buildLoginResponse(u, currentStoreId);
    }

    /**
     * 切换当前会话门店，签发新 JWT（sid claim 更新）。
     */
    @Transactional(readOnly = true)
    public LoginResponse switchStore(String authorizationHeader, Long storeId) {
        SysUser u = loadUserFromBearer(authorizationHeader);
        List<SysStore> accessible = sortedAccessibleStores(u);
        boolean ok = accessible.stream().anyMatch(s -> s.getId().equals(storeId));
        if (!ok) {
            throw new IllegalArgumentException("无权访问该门店或门店不可用");
        }
        return buildLoginResponse(u, storeId);
    }

    /**
     * 当前会话信息（需 Bearer JWT）；若令牌中门店已失效则回退为默认解析规则。
     */
    @Transactional(readOnly = true)
    public AuthProfile session(String authorizationHeader) {
        Claims claims = parseClaims(requireBearerToken(authorizationHeader));
        Long userId = Long.parseLong(claims.getSubject());
        SysUser u = userRepository.loadWithTenantAndStores(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Long sidClaim = JwtCodec.getLongClaim(claims, JwtClaimKeys.STORE_ID);
        Long effective = effectiveCurrentStoreId(u, sidClaim);
        return buildProfile(u, effective);
    }

    /**
     * 设置默认登录门店（持久化）；不自动刷新 JWT，下次登录生效；可选随后调用 switch-store。
     */
    @Transactional
    public AuthProfile updateDefaultStore(String authorizationHeader, DefaultStoreRequest req) {
        Claims claims = parseClaims(requireBearerToken(authorizationHeader));
        Long userId = Long.parseLong(claims.getSubject());
        SysUser u = userRepository.loadWithTenantAndStores(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        List<SysStore> accessible = sortedAccessibleStores(u);
        boolean ok = accessible.stream().anyMatch(s -> s.getId().equals(req.getStoreId()));
        if (!ok) {
            throw new IllegalArgumentException("无权将该门店设为默认");
        }
        SysStore target = accessible.stream()
                .filter(s -> s.getId().equals(req.getStoreId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("门店不存在"));
        u.setDefaultStore(target);
        u.setUpdatedAt(java.time.Instant.now());
        userRepository.save(u);
        Long sidClaim = JwtCodec.getLongClaim(claims, JwtClaimKeys.STORE_ID);
        Long effective = effectiveCurrentStoreId(u, sidClaim);
        return buildProfile(u, effective);
    }

    private SysUser loadUserFromBearer(String authorizationHeader) {
        Claims claims = parseClaims(requireBearerToken(authorizationHeader));
        Long userId = Long.parseLong(claims.getSubject());
        return userRepository.loadWithTenantAndStores(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    private Claims parseClaims(String bearerToken) {
        try {
            return JwtCodec.parseAndVerify(jwtSecurityProperties.getSecret().trim(), bearerToken);
        } catch (Exception e) {
            throw new IllegalArgumentException("令牌无效或已过期");
        }
    }

    private static String requireBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new IllegalArgumentException("缺少 Authorization");
        }
        String prefix = "Bearer ";
        if (!authorizationHeader.regionMatches(true, 0, prefix, 0, prefix.length())) {
            throw new IllegalArgumentException("请使用 Bearer 令牌");
        }
        String raw = authorizationHeader.substring(prefix.length()).trim();
        if (!StringUtils.hasText(raw)) {
            throw new IllegalArgumentException("令牌为空");
        }
        return raw;
    }

    private LoginResponse buildLoginResponse(SysUser u, Long currentStoreId) {
        long ttlMillis = jwtSecurityProperties.getAccessTokenTtl().toMillis();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimKeys.TENANT_ID, u.getTenant().getId());
        claims.put(JwtClaimKeys.TENANT_TYPE, u.getTenant().getTenantType().name());
        if (currentStoreId != null) {
            claims.put(JwtClaimKeys.STORE_ID, currentStoreId);
        }
        String jwt = JwtCodec.createAccessToken(
                jwtSecurityProperties.getSecret().trim(), String.valueOf(u.getId()), ttlMillis, claims);
        AuthProfile profile = buildProfile(u, currentStoreId);
        return LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(ttlMillis / 1000L)
                .profile(profile)
                .build();
    }

    private AuthProfile buildProfile(SysUser u, Long currentStoreId) {
        List<SysStore> accessible = sortedAccessibleStores(u);
        List<StoreBrief> briefs = accessible.stream()
                .map(s -> StoreBrief.builder()
                        .id(s.getId())
                        .storeCode(s.getStoreCode())
                        .storeName(s.getStoreName())
                        .enabled(s.getEnabled())
                        .build())
                .collect(Collectors.toList());
        boolean switchable = u.getTenant().getTenantType() == TenantType.ENTERPRISE_MULTI_STORE
                && accessible.size() > 1;
        return AuthProfile.builder()
                .tenantId(u.getTenant().getId())
                .tenantCode(u.getTenant().getTenantCode())
                .tenantType(u.getTenant().getTenantType().name())
                .currentStoreId(currentStoreId)
                .defaultStoreId(u.getDefaultStore() != null ? u.getDefaultStore().getId() : null)
                .storeSwitchable(switchable)
                .accessibleStores(briefs)
                .build();
    }

    private Long resolveCurrentStoreId(SysUser u) {
        List<SysStore> stores = sortedAccessibleStores(u);
        if (stores.isEmpty()) {
            return null;
        }
        TenantType tt = u.getTenant().getTenantType();
        if (tt == TenantType.SINGLE_STORE || stores.size() == 1) {
            return stores.get(0).getId();
        }
        if (u.getDefaultStore() != null) {
            Long dsid = u.getDefaultStore().getId();
            boolean ok = stores.stream().anyMatch(s -> s.getId().equals(dsid));
            if (ok) {
                return dsid;
            }
        }
        return stores.get(0).getId();
    }

    /**
     * 若 JWT 中的门店仍在可访问列表中则沿用，否则按登录规则重算。
     */
    private Long effectiveCurrentStoreId(SysUser u, Long sidFromJwt) {
        List<SysStore> stores = sortedAccessibleStores(u);
        if (stores.isEmpty()) {
            return null;
        }
        if (sidFromJwt != null && stores.stream().anyMatch(s -> s.getId().equals(sidFromJwt))) {
            return sidFromJwt;
        }
        return resolveCurrentStoreId(u);
    }

    private List<SysStore> sortedAccessibleStores(SysUser u) {
        return u.getStoreAssignments().stream()
                .map(SysUserStore::getStore)
                .filter(s -> Boolean.TRUE.equals(s.getEnabled()))
                .sorted(Comparator.comparing(SysStore::getSortOrder).thenComparing(SysStore::getId))
                .collect(Collectors.toList());
    }
}
