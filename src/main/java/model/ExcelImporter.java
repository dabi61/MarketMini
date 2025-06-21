/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Admin
 */
public class ExcelImporter {

    public static List<String[]> readExcel(File file) {
        List<String[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                int lastCol = row.getLastCellNum();
                String[] rowData = new String[lastCol];

                for (int i = 0; i < lastCol; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData[i] = getCellValue(cell);
                }

                dataList.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }

//    private static String getCellValue(Cell cell) {
//        return switch (cell.getCellType()) {
//            case STRING ->
//                cell.getStringCellValue();
//            case NUMERIC ->
//                DateUtil.isCellDateFormatted(cell)
//                ? cell.getDateCellValue().toString()
//                : String.valueOf((long) cell.getNumericCellValue());
//            case BOOLEAN ->
//                String.valueOf(cell.getBooleanCellValue());
//            case FORMULA ->
//                cell.getCellFormula();
//            case BLANK ->
//                "";
//            default ->
//                cell.toString();
//        };
//    }

    private static String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING ->
                cell.getStringCellValue();
            case NUMERIC ->
                DateUtil.isCellDateFormatted(cell)
                ? new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue())
                : String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN ->
                String.valueOf(cell.getBooleanCellValue());
            case FORMULA ->
                cell.getCellFormula();
            case BLANK ->
                "";
            default ->
                cell.toString();
        };
    }

}
