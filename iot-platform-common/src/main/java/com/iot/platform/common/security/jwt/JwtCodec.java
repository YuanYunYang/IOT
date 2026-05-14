package com.iot.platform.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * HS256 JWT 签发与校验；网关与用户服务须配置相同的 {@code secret}。
 */
public final class JwtCodec {

    private JwtCodec() {
    }

    /**
     * 签发访问令牌。
     *
     * @param secret        共享密钥（任意长度，内部会规范为 HS256 所需长度）
     * @param subject       主题（例如用户 id）
     * @param ttlMillis     有效期（毫秒）
     */
    public static String createAccessToken(String secret, String subject, long ttlMillis) {
        return createAccessToken(secret, subject, ttlMillis, Collections.emptyMap());
    }

    /**
     * 签发访问令牌，并写入额外 claims（如租户、当前门店等）。
     */
    public static String createAccessToken(String secret, String subject, long ttlMillis, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        SecretKey key = signingKey(secret);
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis));
        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.addClaims(extraClaims);
        }
        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * 从 JWT Claims 读取 Long；不存在或非数字时返回 null。
     */
    public static Long getLongClaim(Claims claims, String name) {
        if (claims == null || name == null) {
            return null;
        }
        Object v = claims.get(name);
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        if (v instanceof String) {
            try {
                return Long.parseLong(((String) v).trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从 JWT Claims 读取字符串；不存在时返回 null。
     */
    public static String getStringClaim(Claims claims, String name) {
        if (claims == null || name == null) {
            return null;
        }
        Object v = claims.get(name);
        return v != null ? String.valueOf(v) : null;
    }

    /**
     * 校验签名与过期时间；失败抛出 {@link io.jsonwebtoken.JwtException}。
     */
    public static Claims parseAndVerify(String secret, String token) {
        SecretKey key = signingKey(secret);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static SecretKey signingKey(String secret) {
        byte[] raw = secret != null ? secret.getBytes(StandardCharsets.UTF_8) : new byte[0];
        byte[] keyBytes = raw.length >= 32 ? raw : sha256(raw);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.length > 0 ? input : "iot-platform-jwt".getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
