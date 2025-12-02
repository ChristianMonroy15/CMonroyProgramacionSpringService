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
            String mensaje
    ) {
        try {
            Path path = Path.of(logFilePath);

            // Abrir archivo existente
            FileInputStream fis = new FileInputStream(path.toFile());
            Workbook workbook = new XSSFWorkbook(fis);
            fis.close();

            Sheet sheet = workbook.getSheetAt(0);

            // ESTE ES EL ID CORRECTO A RETORNAR
            int nextRowNum = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(nextRowNum);

            row.createCell(0).setCellValue(nextRowNum);                 // ID
            row.createCell(1).setCellValue(accion);
            row.createCell(2).setCellValue(nombreArchivo);
            row.createCell(3).setCellValue(LocalDateTime.now().format(DTF));
            row.createCell(4).setCellValue(estado);
            row.createCell(5).setCellValue(token != null ? token : "");
            row.createCell(6).setCellValue(mensaje != null ? mensaje : "");

            FileOutputStream fos = new FileOutputStream(path.toFile());
            workbook.write(fos);
            fos.close();
            workbook.close();

            // REGRESAS EL ID DEL REGISTRO
            return nextRowNum;

        } catch (Exception ex) {
            throw new RuntimeException("Error escribiendo en el log XLSX: " + ex.getMessage(), ex);
        }
    }

}
