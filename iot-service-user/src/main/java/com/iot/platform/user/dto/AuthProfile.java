package com.iot.platform.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "登录会话中的租户与门店上下文（切换门店后刷新）")
public class AuthProfile {

    @Schema(description = "租户 ID")
    private Long tenantId;
    @Schema(description = "租户编码（登录传入）")
    private String tenantCode;
    @Schema(description = "租户类型：ENTERPRISE_MULTI_STORE 或 SINGLE_STORE")
    private String tenantType;
    @Schema(description = "当前会话门店 ID；无门店绑定则为 null")
    private Long currentStoreId;
    @Schema(description = "用户设置的默认登录门店 ID")
    private Long defaultStoreId;
    @Schema(description = "是否允许切换门店（企业多门店且绑定多个启用门店时为 true）")
    private boolean storeSwitchable;
    @Schema(description = "当前用户有权访问的门店列表（AI 跨店检索在此集合内选择 storeIds）")
    private List<StoreBrief> accessibleStores;
}
