package com.iot.platform.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class EmptyUtil {

    private EmptyUtil() {
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isBlank(CharSequence s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    public static boolean isEmpty(Optional<?> o) {
        return o == null || !o.isPresent();
    }

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(Object anyArray) {
        if (anyArray == null) {
            return true;
        }
        if (!anyArray.getClass().isArray()) {
            return false;
        }
        return Array.getLength(anyArray) == 0;
    }

    public static <T> T defaultIfNull(T v, T defaultValue) {
        return v != null ? v : defaultValue;
    }
}
