package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

@Service
public class LogService {

    @Value("${app.log.filepath}")
    private String logFilePath;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public synchronized int agregarRegistro(
            String accion,
            String nombreArchivo,
            String estado,
            String token,
            String rutaArchivo,
            String mensaje
    ) {
        try {
            Path path = Path.of(logFilePath);

            FileInputStream file = new FileInputStream(path.toFile());
            Workbook workbook = new XSSFWorkbook(file);
            file.close();

            Sheet sheet = workbook.getSheetAt(0);
            int nextRowNum = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(nextRowNum);

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime expira = token != null ? ahora.plusMinutes(2) : null;

            row.createCell(0).setCellValue(nextRowNum);
            row.createCell(1).setCellValue(accion);
            row.createCell(2).setCellValue(nombreArchivo);
            row.createCell(3).setCellValue(ahora.format(DTF));
            row.createCell(4).setCellValue(estado);
            row.createCell(5).setCellValue(token != null ? token : "");
            row.createCell(6).setCellValue(expira != null ? expira.format(DTF) : "");
            row.createCell(7).setCellValue(rutaArchivo != null ? rutaArchivo : "");
            row.createCell(8).setCellValue(mensaje != null ? mensaje : "");
            
            FileOutputStream fos = new FileOutputStream(path.toFile());
            workbook.write(fos);
            fos.close();
            workbook.close();

            return nextRowNum;

        } catch (Exception ex) {
            throw new RuntimeException("Error escribiendo log XLSX", ex);
        }
    }

    public boolean validarToken(int idLog, String token) {

        try (FileInputStream fis = new FileInputStream(logFilePath); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue; // saltar encabezado
                }
                int idLogExcel = (int) row.getCell(0).getNumericCellValue();

                if (idLogExcel == idLog) {

                    String tokenGuardado = row.getCell(5).getStringCellValue();
                    String expiraStr = row.getCell(6).getStringCellValue();

                    if (tokenGuardado == null || tokenGuardado.isBlank()) {
                        return false;
                    }

                    if (!tokenGuardado.equals(token)) {
                        return false;
                    }

                    LocalDateTime expira = LocalDateTime.parse(expiraStr, DTF);

                    return LocalDateTime.now().isBefore(expira);
                }
            }

            return false;

        } catch (Exception ex) {
            return false;
        }
    }

    public String obtenerRutaArchivo(int idLog) {

        try {
            Path path = Path.of(logFilePath);

            FileInputStream fis = new FileInputStream(path.toFile());
            Workbook workbook = new XSSFWorkbook(fis);
            fis.close();

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(idLog);

            if (row == null) {
                workbook.close();
                throw new RuntimeException("No existe registro de log con id: " + idLog);
            }

            Cell rutaCell = row.getCell(7); // columna rutaArchivo
            if (rutaCell == null || rutaCell.getStringCellValue().isBlank()) {
                workbook.close();
                throw new RuntimeException("No se encontró ruta de archivo en el log");
            }

            String rutaArchivo = rutaCell.getStringCellValue();
            workbook.close();

            return rutaArchivo;

        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener ruta del archivo desde el log: " + ex.getMessage(), ex);
        }
    }

//    private Row buscarFilaPorId(Sheet sheet, int idLog) {
//        for (Row row : sheet) {
//
//            Cell idCell = row.getCell(0); // columna ID
//
//            if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
//                int id = (int) idCell.getNumericCellValue();
//                if (id == idLog) {
//                    return row;
//                }
//            }
//        }
//        return null;
//    }
}
