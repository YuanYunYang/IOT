package com.iot.platform.common.security.jwt;

import io.jsonwebtoken.JwtException;

import javax.servlet.http.HttpServletRequest;

/**
 * 从请求解析 Bearer JWT 并用 {@link JwtCodec} 校验。
 */
public final class JwtBearerTokens {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private JwtBearerTokens() {
    }

    /**
     * 是否配置了 JWT 校验且当前请求携带合法、未过期的 Bearer JWT。
     */
    public static boolean requestHasValidBearerJwt(JwtSecurityProperties jwt, HttpServletRequest request) {
        if (jwt == null || !jwt.isValidateBearer()) {
            return false;
        }
        String secret = jwt.getSecret();
        if (secret == null || secret.trim().isEmpty()) {
            return false;
        }
        String raw = extractBearerToken(request);
        if (raw == null || raw.isEmpty()) {
            return false;
        }
        try {
            JwtCodec.parseAndVerify(secret.trim(), raw.trim());
            return true;
        } catch (JwtException | IllegalArgumentException | IllegalStateException ignored) {
            return false;
        }
    }

    /**
     * 解析 {@code Authorization: Bearer &lt;token&gt;}；不存在或格式不对则返回 null。
     */
    public static String extractBearerToken(HttpServletRequest request) {
        String auth = request.getHeader(AUTHORIZATION);
        if (auth == null || auth.isEmpty()) {
            return null;
        }
        if (auth.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return auth.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
