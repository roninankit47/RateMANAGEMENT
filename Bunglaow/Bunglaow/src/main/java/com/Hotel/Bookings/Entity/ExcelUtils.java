package com.Hotel.Bookings.Entity;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    public static byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    public static Workbook readWorkbookFromBytes(byte[] bytes) throws IOException {
        return WorkbookFactory.create(new ByteArrayInputStream(bytes));
    }

    public static void writeRatesToSheet(Workbook workbook, Iterable<Rate> rates) {
        Sheet sheet = workbook.createSheet("Rates");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Stay Date From");
        headerRow.createCell(1).setCellValue("Stay Date To");
        headerRow.createCell(2).setCellValue("Nights");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Bungalow ID");
        headerRow.createCell(5).setCellValue("Closed Date");

        // Create data rows
        int rowNum = 1;
        for (Rate rate : rates) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rate.getStayDateFrom().toString());
            row.createCell(1).setCellValue(rate.getStayDateTo().toString());
            row.createCell(2).setCellValue(rate.getNights());
            row.createCell(3).setCellValue(rate.getValue());
            row.createCell(4).setCellValue(rate.getBungalowId());
            if (rate.getClosedDate() != null) {
                row.createCell(5).setCellValue(rate.getClosedDate().toString());
            } else {
                row.createCell(5).setCellValue(""); // Handle the case where Closed Date is null
            }
        }
    }

    public static Iterable<Rate> readRatesFromSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Rates");
        Iterator<Row> rowIterator = sheet.iterator();

        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        List<Rate> rates = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Rate rate = new Rate();
            rate.setStayDateFrom(LocalDate.parse(row.getCell(0).getStringCellValue()));
            rate.setStayDateTo(LocalDate.parse(row.getCell(1).getStringCellValue()));
            rate.setNights((int) row.getCell(2).getNumericCellValue());
            rate.setValue(row.getCell(3).getNumericCellValue());
            rate.setBungalowId((long) row.getCell(4).getNumericCellValue());
            Cell closedDateCell = row.getCell(5);
            if (closedDateCell != null && !closedDateCell.getStringCellValue().isEmpty()) {
                rate.setClosedDate(LocalDateTime.parse(closedDateCell.getStringCellValue()));
            } else {
                rate.setClosedDate(null); // Set Closed Date as null if the cell is empty or null
            }
            rates.add(rate);
        }

        return rates;
    }

    public static Iterable<Rate> readRatesFromExcelFile(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            return readRatesFromSheet(workbook);
        }
    }
}
