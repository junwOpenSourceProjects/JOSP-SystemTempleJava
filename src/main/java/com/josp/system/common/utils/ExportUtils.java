package com.josp.system.common.utils;

import com.josp.system.entity.LoginLog;
import com.josp.system.entity.OperLog;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Excel导出工具类
 */
public class ExportUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出登录日志到Excel
     */
    public static void exportLoginLogs(java.util.List<LoginLog> loginLogs, HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode("登录日志_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (Workbook workbook = new SXSSFWorkbook(1000)) {
            Sheet sheet = workbook.createSheet("登录日志");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"用户名", "IP地址", "登录地点", "浏览器", "操作系统", "登录状态", "提示消息", "登录时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // 填充数据
            int rowNum = 1;
            for (LoginLog log : loginLogs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getUsername() != null ? log.getUsername() : "");
                row.createCell(1).setCellValue(log.getIp() != null ? log.getIp() : "");
                row.createCell(2).setCellValue(log.getAddress() != null ? log.getAddress() : "");
                row.createCell(3).setCellValue(log.getBrowser() != null ? log.getBrowser() : "");
                row.createCell(4).setCellValue(log.getOs() != null ? log.getOs() : "");
                row.createCell(5).setCellValue(log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");
                row.createCell(6).setCellValue(log.getMsg() != null ? log.getMsg() : "");
                row.createCell(7).setCellValue(log.getLoginTime() != null ? log.getLoginTime().format(DATE_FORMATTER) : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 导出操作日志到Excel
     */
    public static void exportOperLogs(java.util.List<OperLog> operLogs, HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode("操作日志_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (Workbook workbook = new SXSSFWorkbook(1000)) {
            Sheet sheet = workbook.createSheet("操作日志");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"操作标题", "业务类型", "请求方法", "请求方式", "操作人", "部门名称", "请求URL", "操作IP", "操作地点", "操作状态", "消耗时间(ms)", "操作时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // 填充数据
            int rowNum = 1;
            for (OperLog log : operLogs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getTitle() != null ? log.getTitle() : "");
                row.createCell(1).setCellValue(log.getBusinessType() != null ? log.getBusinessType() : "");
                row.createCell(2).setCellValue(log.getMethod() != null ? log.getMethod() : "");
                row.createCell(3).setCellValue(log.getRequestMethod() != null ? log.getRequestMethod() : "");
                row.createCell(4).setCellValue(log.getOperName() != null ? log.getOperName() : "");
                row.createCell(5).setCellValue(log.getDeptName() != null ? log.getDeptName() : "");
                row.createCell(6).setCellValue(log.getOperUrl() != null ? log.getOperUrl() : "");
                row.createCell(7).setCellValue(log.getOperIp() != null ? log.getOperIp() : "");
                row.createCell(8).setCellValue(log.getOperLocation() != null ? log.getOperLocation() : "");
                row.createCell(9).setCellValue(log.getStatus() != null && log.getStatus() == 1 ? "正常" : "异常");
                row.createCell(10).setCellValue(log.getCostTime() != null ? log.getCostTime().toString() : "0");
                row.createCell(11).setCellValue(log.getOperTime() != null ? log.getOperTime().format(DATE_FORMATTER) : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
