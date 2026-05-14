package com.iot.platform.alarm.domain;

/**
 * 报警规则中「当前点位值」与「阈值」的比较关系。
 */
public enum CompareOperator {
    /** 大于 */
    GT,
    /** 大于等于 */
    GTE,
    /** 小于 */
    LT,
    /** 小于等于 */
    LTE,
    /** 等于 */
    EQ
}
