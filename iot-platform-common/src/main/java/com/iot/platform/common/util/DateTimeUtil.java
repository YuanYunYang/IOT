package com.iot.platform.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Java 8 日期时间工具：解析、格式化、常见模式互转。
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    public static final List<String> COMMON_PATTERNS = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "yyyyMMddHHmmss",
            "HH:mm:ss"
    );

    public static String format(LocalDateTime dt) {
        return format(dt, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(LocalDateTime dt, String pattern) {
        if (dt == null) {
            return null;
        }
        return dt.format(DateTimeFormatter.ofPattern(pattern, Locale.ROOT));
    }

    public static String format(LocalDate d) {
        return format(d, "yyyy-MM-dd");
    }

    public static String format(LocalDate d, String pattern) {
        if (d == null) {
            return null;
        }
        return d.format(DateTimeFormatter.ofPattern(pattern, Locale.ROOT));
    }

    public static String format(Instant instant) {
        return format(instant, DEFAULT_ZONE, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Instant instant, ZoneId zone, String pattern) {
        if (instant == null) {
            return null;
        }
        ZoneId z = zone != null ? zone : DEFAULT_ZONE;
        return DateTimeFormatter.ofPattern(pattern, Locale.ROOT)
                .withZone(z)
                .format(instant);
    }

    public static Optional<LocalDateTime> tryParseLocalDateTime(String text) {
        return tryParseLocalDateTime(text, COMMON_PATTERNS);
    }

    public static Optional<LocalDateTime> tryParseLocalDateTime(String text, List<String> patterns) {
        if (EmptyUtil.isBlank(text)) {
            return Optional.empty();
        }
        List<String> pats = patterns == null || patterns.isEmpty() ? COMMON_PATTERNS : patterns;
        for (String p : pats) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern(p, Locale.ROOT);
                TemporalAccessor ta = f.parse(text.trim());
                // 若模式仅含日期，则补到当天 0 点
                if (ta.isSupported(java.time.temporal.ChronoField.HOUR_OF_DAY)) {
                    return Optional.of(LocalDateTime.from(ta));
                }
                return Optional.of(LocalDate.from(ta).atStartOfDay());
            } catch (DateTimeParseException ignore) {
                // 换下一套模式重试
            }
        }
        // 再尝试 ISO_INSTANT / 带偏移的日期时间
        try {
            Instant i = Instant.parse(text.trim());
            return Optional.of(LocalDateTime.ofInstant(i, DEFAULT_ZONE));
        } catch (Exception ignore) {
        }
        try {
            OffsetDateTime odt = OffsetDateTime.parse(text.trim());
            return Optional.of(odt.toLocalDateTime());
        } catch (Exception ignore) {
        }
        return Optional.empty();
    }

    public static LocalDateTime parseLocalDateTime(String text) {
        return tryParseLocalDateTime(text).orElseThrow(() -> new IllegalArgumentException("Unparseable datetime: " + text));
    }

    public static Optional<LocalDate> tryParseLocalDate(String text) {
        if (EmptyUtil.isBlank(text)) {
            return Optional.empty();
        }
        for (String p : COMMON_PATTERNS) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern(p, Locale.ROOT);
                TemporalAccessor ta = f.parse(text.trim());
                if (ta.isSupported(java.time.temporal.ChronoField.YEAR)
                        && ta.isSupported(java.time.temporal.ChronoField.MONTH_OF_YEAR)
                        && ta.isSupported(java.time.temporal.ChronoField.DAY_OF_MONTH)) {
                    return Optional.of(LocalDate.from(ta));
                }
            } catch (DateTimeParseException ignore) {
            }
        }
        return Optional.empty();
    }

    public static Instant toInstant(LocalDateTime dt) {
        return toInstant(dt, DEFAULT_ZONE);
    }

    public static Instant toInstant(LocalDateTime dt, ZoneId zone) {
        if (dt == null) {
            return null;
        }
        ZoneId z = zone != null ? zone : DEFAULT_ZONE;
        return dt.atZone(z).toInstant();
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return toLocalDateTime(instant, DEFAULT_ZONE);
    }

    public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zone) {
        if (instant == null) {
            return null;
        }
        ZoneId z = zone != null ? zone : DEFAULT_ZONE;
        return LocalDateTime.ofInstant(instant, z);
    }

    public static Date toDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    public static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }

    public static long toEpochMillis(Instant instant) {
        return instant == null ? 0L : instant.toEpochMilli();
    }

    public static Instant fromEpochMillis(Long epochMillis) {
        return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
    }
}
