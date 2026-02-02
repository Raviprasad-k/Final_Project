package com.zigwheels.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ExcelUtils {
    private final String filePath;
    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    public ExcelUtils(String filePath) {
        this.filePath = filePath;
        ensureWorkbook();
    }

    private synchronized void ensureWorkbook() {
        File f = new File(filePath);
        if (!f.exists()) {
            try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
                wb.write(fos);
                logger.info("Created workbook: {}", filePath);
            } catch (IOException e) {
                logger.error("Failed to create workbook {}: {}", filePath, e.getMessage());
            }
        }
    }

    public synchronized void writeSheet(String sheetName, List<String> headers, List<List<String>> rows) {
        try (FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = wb.getSheet(sheetName);
            if (sheet != null) {
                int idx = wb.getSheetIndex(sheet);
                wb.removeSheetAt(idx);
            }
            sheet = wb.createSheet(sheetName);
            int r = 0;

            // 1. Header Logic
            if (headers != null && !headers.isEmpty()) {
                Row headerRow = sheet.createRow(r++);
                // Create a bold style for headers to make them look "aligned" and distinct
                CellStyle headerStyle = wb.createCellStyle();
                Font font = wb.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                for (int c = 0; c < headers.size(); c++) {
                    Cell cell = headerRow.createCell(c);
                    cell.setCellValue(headers.get(c));
                    cell.setCellStyle(headerStyle);
                }
            }

            // 2. Rows Logic
            int maxColumns = (headers != null) ? headers.size() : 0; // Track max columns for autosizing
            if (rows != null) {
                for (List<String> rowVals : rows) {
                    Row row = sheet.createRow(r++);
                    if (rowVals.size() > maxColumns) maxColumns = rowVals.size(); // Update max columns found
                    
                    for (int c = 0; c < rowVals.size(); c++) {
                        row.createCell(c).setCellValue(rowVals.get(c));
                    }
                }
            }

            // 3. IMPROVED AUTOSIZE LOGIC
            // Previously, this only ran if headers != null. 
            // Now it runs based on the actual data width to prevent "..." truncation.
            int columnsToSize = Math.max(maxColumns, (headers != null ? headers.size() : 0));
            for (int c = 0; c < columnsToSize; c++) {
                sheet.autoSizeColumn(c);
                // Optional: Add a small padding because autoSizeColumn can be tight
                int currentWidth = sheet.getColumnWidth(c);
                sheet.setColumnWidth(c, currentWidth + 500); 
            }
            
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
            logger.info("Wrote sheet '{}' with {} rows into {}", sheetName, rows==null?0:rows.size(), filePath);
        } catch (IOException e) {
            logger.error("Error writing sheet {}: {}", sheetName, e.getMessage());
        }
    }
}


//
//package com.zigwheels.utils;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.*;
//import java.util.List;
//
//public class ExcelUtils {
//    private final String filePath;
//    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);
//
//    public ExcelUtils(String filePath) {
//        this.filePath = filePath;
//        ensureWorkbook();
//    }
//
//    private synchronized void ensureWorkbook() {
//        File f = new File(filePath);
//        if (!f.exists()) {
//            try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
//                wb.write(fos);
//                logger.info("Created workbook: {}", filePath);
//            } catch (IOException e) {
//                logger.error("Failed to create workbook {}: {}", filePath, e.getMessage());
//            }
//        }
//    }
//
//    public synchronized void writeSheet(String sheetName, List<String> headers, List<List<String>> rows) {
//        try (FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook wb = new XSSFWorkbook(fis)) {
//            XSSFSheet sheet = wb.getSheet(sheetName);
//            if (sheet != null) {
//                int idx = wb.getSheetIndex(sheet);
//                wb.removeSheetAt(idx);
//            }
//            sheet = wb.createSheet(sheetName);
//            int r = 0;
//            // header
//            if (headers != null && !headers.isEmpty()) {
//                Row header = sheet.createRow(r++);
//                for (int c = 0; c < headers.size(); c++) {
//                    Cell cell = header.createCell(c);
//                    cell.setCellValue(headers.get(c));
//                }
//            }
//            // rows
//            if (rows != null) {
//                for (List<String> rowVals : rows) {
//                    Row row = sheet.createRow(r++);
//                    for (int c = 0; c < rowVals.size(); c++) {
//                        row.createCell(c).setCellValue(rowVals.get(c));
//                    }
//                }
//            }
//            // autosize
//            if (headers != null) {
//                for (int c = 0; c < headers.size(); c++) {
//                    sheet.autoSizeColumn(c);
//                }
//            }
//            
//            try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                wb.write(fos);
//            }
//            logger.info("Wrote sheet '{}' with {} rows into {}", sheetName, rows==null?0:rows.size(), filePath);
//        } catch (IOException e) {
//            logger.error("Error writing sheet {}: {}", sheetName, e.getMessage());
//        }
//    }
//}
