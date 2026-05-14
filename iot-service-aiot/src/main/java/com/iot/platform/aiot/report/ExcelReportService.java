package com.iot.platform.aiot.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 将行列表导出为 xlsx；单表、表头行、普通单元格。
 */
@Service
public class ExcelReportService {

    public byte[] toXlsx(String sheetName, List<Map<String, Object>> rows) {
        String safeSheetName = (sheetName == null || sheetName.trim().isEmpty()) ? "Report" : sheetName.trim();
        if (safeSheetName.length() > 31) {
            safeSheetName = safeSheetName.substring(0, 31);
        }

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet(safeSheetName);

            List<String> columns = inferColumns(rows);

            // 表头样式
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);

            int r = 0;
            Row header = sheet.createRow(r++);
            for (int i = 0; i < columns.size(); i++) {
                Cell c = header.createCell(i);
                c.setCellValue(columns.get(i));
                c.setCellStyle(headerStyle);
            }

            for (Map<String, Object> row : rows) {
                Row rr = sheet.createRow(r++);
                for (int i = 0; i < columns.size(); i++) {
                    Object v = row != null ? row.get(columns.get(i)) : null;
                    Cell c = rr.createCell(i);
                    c.setCellValue(v == null ? "" : String.valueOf(v));
                }
            }

            // 冻结表头行
            sheet.createFreezePane(0, 1);

            // 列宽自适应（限制列数以免开销过大）
            int cap = Math.min(columns.size(), 30);
            for (int i = 0; i < cap; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("生成 xlsx 失败：" + e.getMessage(), e);
        }
    }

    private static List<String> inferColumns(List<Map<String, Object>> rows) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row != null) {
                    set.addAll(row.keySet());
                }
            }
        }
        if (set.isEmpty()) {
            List<String> fallback = new ArrayList<>();
            fallback.add("empty");
            return fallback;
        }
        return new ArrayList<>(set);
    }

    public static String safeFilename(String filename, String defaultName) {
        String name = filename == null || filename.trim().isEmpty() ? defaultName : filename.trim();
        // 保守清洗：保留 ASCII 与中文常用字符范围外的替换见正则
        name = name.replaceAll("[\\\\/:*?\"<>|]+", "_");
        if (!name.toLowerCase().endsWith(".xlsx")) {
            name = name + ".xlsx";
        }
        // 避免替换后文件名为空
        if (name.trim().isEmpty()) {
            name = defaultName + ".xlsx";
        }
        // 文件名 UTF-8 往返保证可作为附件名；HTTP 头编码由控制器设置
        return new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }
}

