package com.iot.platform.common.security.jwt;

/**
 * JWT 自定义声明键（短键名降低体积）；与用户服务签发逻辑保持一致。
 */
public final class JwtClaimKeys {

    /** 租户主键 ID */
    public static final String TENANT_ID = "tid";
    /** 租户类型，字符串枚举名，如 ENTERPRISE_MULTI_STORE */
    public static final String TENANT_TYPE = "ttp";
    /** 当前会话门店 ID（切换门店后刷新） */
    public static final String STORE_ID = "sid";

    private JwtClaimKeys() {
    }
}
