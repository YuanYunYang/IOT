package com.iot.platform.aiot.kb;

import com.iot.platform.aiot.client.UserAuthSessionClient;
import com.iot.platform.aiot.kb.dto.AuthProfileDto;
import com.iot.platform.aiot.kb.dto.StoreBriefDto;
import com.iot.platform.common.api.ApiResult;
import com.iot.platform.common.security.jwt.JwtClaimKeys;
import com.iot.platform.common.security.jwt.JwtCodec;
import com.iot.platform.common.security.jwt.JwtSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根据 JWT 与用户服务 session，解析知识库检索允许的门店 ID 集合。
 */
@Component
@RequiredArgsConstructor
public class KbScopeResolver {

    private final UserAuthSessionClient userAuthSessionClient;
    private final JwtSecurityProperties jwtSecurityProperties;

    /**
     * @param authorizationHeader 完整 Authorization 头（含 Bearer）
     * @param requestedStoreIds   调用方显式指定的门店；null 或空表示使用全部可访问门店
     * @return 租户 ID + 经权限裁剪后的门店 ID 列表（非空）
     */
    public KbScope resolve(String authorizationHeader, List<Long> requestedStoreIds) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new IllegalArgumentException("缺少 Authorization");
        }
        String bearer = authorizationHeader.trim();
        if (!bearer.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            throw new IllegalArgumentException("请使用 Bearer 令牌");
        }
        String token = bearer.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(jwtSecurityProperties.getSecret())) {
            throw new IllegalStateException("未配置 iot.security.jwt.secret");
        }
        Claims claims;
        try {
            claims = JwtCodec.parseAndVerify(jwtSecurityProperties.getSecret().trim(), token);
        } catch (JwtException e) {
            throw new IllegalArgumentException("令牌无效或已过期");
        }
        Long jwtTenantId = JwtCodec.getLongClaim(claims, JwtClaimKeys.TENANT_ID);
        if (jwtTenantId == null) {
            throw new IllegalArgumentException("令牌缺少租户信息");
        }

        ApiResult<AuthProfileDto> sessionResp = userAuthSessionClient.session(authorizationHeader.trim());
        if (sessionResp == null || !sessionResp.isSuccess() || sessionResp.getData() == null) {
            throw new IllegalStateException("无法获取用户会话：" + (sessionResp != null ? sessionResp.getMessage() : "null"));
        }
        AuthProfileDto profile = sessionResp.getData();
        if (!Objects.equals(profile.getTenantId(), jwtTenantId)) {
            throw new IllegalArgumentException("令牌与会话租户不一致");
        }

        Set<Long> accessible = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(profile.getAccessibleStores())) {
            for (StoreBriefDto s : profile.getAccessibleStores()) {
                if (s != null && s.getId() != null && Boolean.TRUE.equals(s.getEnabled())) {
                    accessible.add(s.getId());
                }
            }
        }
        if (accessible.isEmpty()) {
            throw new IllegalArgumentException("当前用户未绑定任何可用门店，无法检索知识库");
        }

        Set<Long> effective;
        if (CollectionUtils.isEmpty(requestedStoreIds)) {
            effective = new LinkedHashSet<>(accessible);
        } else {
            effective = new LinkedHashSet<>();
            for (Long id : requestedStoreIds) {
                if (id != null && accessible.contains(id)) {
                    effective.add(id);
                }
            }
            if (effective.isEmpty()) {
                throw new IllegalArgumentException("请求的门店均不在当前用户可访问范围内");
            }
        }

        return new KbScope(jwtTenantId, effective.stream().collect(Collectors.toList()));
    }

    public static final class KbScope {
        private final long tenantId;
        private final List<Long> storeIds;

        public KbScope(long tenantId, List<Long> storeIds) {
            this.tenantId = tenantId;
            this.storeIds = storeIds;
        }

        public long getTenantId() {
            return tenantId;
        }

        public List<Long> getStoreIds() {
            return storeIds;
        }
    }
}
