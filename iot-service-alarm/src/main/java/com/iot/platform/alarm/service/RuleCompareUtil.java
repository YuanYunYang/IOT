package com.iot.platform.alarm.service;

import com.iot.platform.alarm.domain.CompareOperator;

import java.math.BigDecimal;

final class RuleCompareUtil {

    private RuleCompareUtil() {
    }

    static double parseNumeric(String raw) {
        if (raw == null) {
            return Double.NaN;
        }
        String s = raw.trim().replaceAll("[^0-9.+-Ee]", "");
        if (s.isEmpty()) {
            return Double.NaN;
        }
        try {
            return new BigDecimal(s).doubleValue();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    static boolean compare(double value, double threshold, CompareOperator op) {
        if (Double.isNaN(value)) {
            return false;
        }
        switch (op) {
            case GT:
                return value > threshold;
            case GTE:
                return value >= threshold;
            case LT:
                return value < threshold;
            case LTE:
                return value <= threshold;
            case EQ:
                return Double.compare(value, threshold) == 0;
            default:
                return false;
        }
    }
}
