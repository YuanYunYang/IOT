package com.iot.platform.user.domain;

/**
 * SaaS 租户形态：企业多门店（总部多门店切换）或单门店租户。
 */
public enum TenantType {

    /** 企业级：多个门店，用户可绑定多个门店并可切换当前门店 */
    ENTERPRISE_MULTI_STORE,

    /** 单门店租户：租户下仅一个门店，用户登录后无切换概念 */
    SINGLE_STORE
}
