package com.iot.platform.common.api;

/**
 * 业务错误码契约。各服务可自建枚举实现本接口并集中维护（例如 XxxErrorCode）。
 * <p>
 * 建议分段占用，避免冲突：
 * <ul>
 *   <li>0：成功</li>
 *   <li>1000–1999：平台通用（见 PlatformErrorCode）</li>
 *   <li>10000+：按服务划分（例如 AIOT 使用 12000–12999）</li>
 * </ul>
 */
public interface ErrorCode {

    /** 数值错误码，需全局唯一（在平台内约定分段即可）。 */
    int getCode();

    /** 默认提示文案；可在返回时按场景覆盖。 */
    String getMessage();
}
