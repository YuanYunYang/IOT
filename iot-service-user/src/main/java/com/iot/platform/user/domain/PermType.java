package com.iot.platform.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "权限类型：菜单/目录、页面、按钮")
public enum PermType {
    /** 菜单 / 目录 */
    @Schema(description = "菜单或目录节点")
    MENU,
    /** 页面 */
    @Schema(description = "页面级权限")
    PAGE,
    /** 按钮级（如 新增/删除） */
    @Schema(description = "按钮或操作级权限")
    BUTTON
}
