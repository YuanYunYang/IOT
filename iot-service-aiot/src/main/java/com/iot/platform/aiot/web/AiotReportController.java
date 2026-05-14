package com.iot.platform.aiot.web;

import com.iot.platform.aiot.api.AiotErrorCode;
import com.iot.platform.aiot.report.CachedReport;
import com.iot.platform.aiot.report.ExcelReportService;
import com.iot.platform.aiot.report.ReportCacheService;
import com.iot.platform.common.api.ApiResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

/**
 * 用户确认后，将缓存的查询结果下载为 Excel。
 */
@RestController
@RequestMapping("/api/v1")
public class AiotReportController {

    private final ReportCacheService cacheService;
    private final ExcelReportService excelReportService;

    public AiotReportController(ReportCacheService cacheService, ExcelReportService excelReportService) {
        this.cacheService = cacheService;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/reports/download/{reportId}")
    public ResponseEntity<?> download(@PathVariable("reportId") String reportId) {
        CachedReport cached = cacheService.get(reportId);
        if (cached == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ApiResult.fail(AiotErrorCode.REPORT_NOT_FOUND));
        }
        String filename = ExcelReportService.safeFilename(cached.getFilename(), "aiot-report");
        byte[] bytes = excelReportService.toXlsx("AIOT", cached.getRows());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        // 附件文件名按 RFC 5987 编码
        String encoded;
        try {
            encoded = java.net.URLEncoder.encode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // JVM 中 UTF-8 应始终存在；异常时退化为原始文件名
            encoded = filename;
        }
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}

