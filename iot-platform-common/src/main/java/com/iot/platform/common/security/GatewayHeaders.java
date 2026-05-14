package com.iot.platform.common.security;

/**
 * 网关与下游微服务之间约定的 HTTP 请求头名称常量。
 * <p>
 * 下游仅在确认请求来自 iot-gateway 时才应信任这些头（例如比对 HEADER_GATEWAY_SECRET）。
 */
public final class GatewayHeaders {

    private GatewayHeaders() {
    }

    /** 标记请求已由网关完成鉴权 */
    public static final String HEADER_GATEWAY_VERIFIED = "X-Gateway-Verified";

    /**
     * 网关注入的共享密钥；下游与本地配置比对，防止绕过网关伪造请求。
     */
    public static final String HEADER_GATEWAY_SECRET = "X-Gateway-Secret";

    /** Optional: user id propagated from gateway. */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** 可选：由网关透传的用户名 */
    public static final String HEADER_USERNAME = "X-Username";
}

