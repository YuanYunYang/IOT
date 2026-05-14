package com.iot.platform.aiot.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * LLM 生成 SQL 的轻量安全闸：仅允许单条 SELECT/WITH、禁止注释与危险关键字、强制 LIMIT 且上限 500。
 * <p>
 * 非完整 SQL 解析器，规则偏保守；漏网之鱼需结合库账号只读权限兜底。
 */
public class SqlSafetyValidator {

    private static final Pattern MULTI_STMT = Pattern.compile(";\\s*\\S", Pattern.DOTALL);
    private static final Pattern COMMENT = Pattern.compile("(--|/\\*|\\*/|#)");
    private static final Pattern LIMIT_NUM = Pattern.compile("(?i)\\blimit\\s+(\\d+)\\b");

    private static final Set<String> FORBIDDEN = new HashSet<>(Arrays.asList(
            "insert", "update", "delete", "merge", "replace",
            "alter", "drop", "truncate", "create", "grant", "revoke",
            "attach", "detach", "optimize", "system", "kill"
    ));

    public void validateSelectOnly(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL 为空");
        }
        String s = sql.trim();
        String lower = s.toLowerCase(Locale.ROOT);

        if (!lower.startsWith("select") && !lower.startsWith("with")) {
            throw new IllegalArgumentException("仅允许 SELECT 或 WITH 开头的查询");
        }

        if (MULTI_STMT.matcher(s).find()) {
            throw new IllegalArgumentException("不允许多条语句");
        }

        if (COMMENT.matcher(s).find()) {
            throw new IllegalArgumentException("不允许 SQL 注释");
        }

        for (String kw : FORBIDDEN) {
            if (containsWord(lower, kw)) {
                throw new IllegalArgumentException("禁止使用关键字：" + kw);
            }
        }

        if (!containsWord(lower, "limit")) {
            throw new IllegalArgumentException("必须包含 LIMIT");
        }
        int limit = extractLimit(lower);
        if (limit <= 0) {
            throw new IllegalArgumentException("LIMIT 必须为正整数");
        }
        if (limit > 500) {
            throw new IllegalArgumentException("LIMIT 过大（最大 500）");
        }
    }

    private static int extractLimit(String lowerSql) {
        Matcher m = LIMIT_NUM.matcher(lowerSql);
        int found = 0;
        int val = -1;
        while (m.find()) {
            found++;
            try {
                val = Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {
                val = -1;
            }
        }
        if (found != 1) {
            // 保证有唯一 LIMIT，便于确定扫描行数上限；建议在语句末尾写一个 LIMIT
            throw new IllegalArgumentException("SQL 中必须且只能出现一处 LIMIT");
        }
        return val;
    }

    private static boolean containsWord(String text, String word) {
        // 简易词边界：关键字两侧不能紧贴字母数字或下划线
        int idx = text.indexOf(word);
        while (idx >= 0) {
            boolean leftOk = idx == 0 || !isWordChar(text.charAt(idx - 1));
            int end = idx + word.length();
            boolean rightOk = end >= text.length() || !isWordChar(text.charAt(end));
            if (leftOk && rightOk) {
                return true;
            }
            idx = text.indexOf(word, idx + 1);
        }
        return false;
    }

    private static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
}

