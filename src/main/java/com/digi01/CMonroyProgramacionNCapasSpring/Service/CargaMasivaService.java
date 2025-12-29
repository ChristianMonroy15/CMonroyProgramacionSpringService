package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import com.digi01.CMonroyProgramacionNCapasSpring.DTO.ResultLog;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.ErrorCarga;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.RolJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import java.io.BufferedReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Service
public class CargaMasivaService {

    @Autowired
    private LogService logService;

    @Autowired
    private ValidationService validationService;

    public String guardarArchivo(MultipartFile archivo) throws Exception {

        String extension = archivo.getOriginalFilename().split("\\.")[1];

        String path = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivosCarga";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
        String pathDefinitvo = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();

        try {
            archivo.transferTo(new File(pathDefinitvo));

        } catch (Exception ex) {
            String errortransferencia = ex.getLocalizedMessage();

        }

        return pathDefinitvo; // Se guarda la ruta para el siguiente paso
    }

    public ResultLog validarArchivo(String rutaArchivo) {

        ResultLog resultado = new ResultLog();

        try {
            File file = new File(rutaArchivo);

            if (!file.exists()) {
                resultado.setCorrect(false);
                resultado.setMensaje("El archivo no existe");
                return resultado;
            }

            List<UsuarioJPA> usuarios;

            if (rutaArchivo.endsWith(".txt")) {
                usuarios = LecturaArchivoTXT(file);
            } else if (rutaArchivo.endsWith(".xlsx")) {
                usuarios = LecturaArchivoXLSX(file);
            } else {

                int idLogValidar = logService.agregarRegistro(
                        "VALIDAR",
                        file.getName(),
                        "ERROR_VALIDACION",
                        null,
                        rutaArchivo, // 👈 SE CONSERVA
                        "Extensión no soportada"
                );

                resultado.setCorrect(false);
                resultado.setIdLog(idLogValidar);
                resultado.setMensaje("Extensión no soportada");
                return resultado;
            }

            // Validación de lectura
            if (usuarios == null) {

                int idLogValidar = logService.agregarRegistro(
                        "VALIDAR",
                        file.getName(),
                        "ERROR_VALIDACION",
                        null,
                        rutaArchivo,
                        "Error al leer el archivo"
                );

                resultado.setCorrect(false);
                resultado.setIdLog(idLogValidar);
                resultado.setMensaje("Error al leer el archivo");
                return resultado;
            }

            // Validación de datos
            List<ErrorCarga> errores = ValidarDatosArchivo(usuarios);

            if (!errores.isEmpty()) {

                int idLogValidar = logService.agregarRegistro(
                        "VALIDAR",
                        file.getName(),
                        "ERROR_VALIDACION",
                        null,
                        rutaArchivo,
                        "Errores en los datos del archivo"
                );

                resultado.setCorrect(false);
                resultado.setIdLog(idLogValidar);
                resultado.setErrores(errores);
                resultado.setMensaje("Errores en el archivo");
                return resultado;
            }

            // ✔️ VALIDADO
            String token = UUID.randomUUID().toString();

            int idLogValidar = logService.agregarRegistro(
                    "VALIDAR",
                    file.getName(),
                    "VALIDADO",
                    token,
                    rutaArchivo, // 👈 CLAVE
                    "Archivo validado correctamente"
            );

            resultado.setCorrect(true);
            resultado.setIdLog(idLogValidar);
            resultado.setToken(token);
            resultado.setMensaje("Archivo validado correctamente");

            return resultado;

        } catch (Exception ex) {

            int idLogValidar = logService.agregarRegistro(
                    "VALIDAR",
                    new File(rutaArchivo).getName(),
                    "ERROR_VALIDACION",
                    null,
                    rutaArchivo,
                    ex.getMessage()
            );

            resultado.setCorrect(false);
            resultado.setIdLog(idLogValidar);
            resultado.setMensaje("Error al validar");
            return resultado;
        }
    }

    public List<UsuarioJPA> LecturaArchivoTXT(File archivo) {

        List<UsuarioJPA> usuarios = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(archivo); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String linea;
            int lineaActual = 0;

            while ((linea = bufferedReader.readLine()) != null) {
                lineaActual++;

                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split("\\|", -1); // -1 mantiene campos vacíos
                UsuarioJPA usuario = new UsuarioJPA();

                usuario.setNombre(campos.length > 0 ? campos[0].trim() : "");
                usuario.setApellidoPaterno(campos.length > 1 ? campos[1].trim() : "");
                usuario.setApellidoMaterno(campos.length > 2 ? campos[2].trim() : "");
                usuario.setUserName(campos.length > 3 ? campos[3].trim() : "");
                usuario.setEmail(campos.length > 4 ? campos[4].trim() : "");
                usuario.setPassword(campos.length > 5 ? campos[5].trim() : "");

                // Fecha (NO romper si viene mal)
                if (campos.length > 6 && !campos[6].trim().isEmpty()) {
                    try {
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        usuario.setFechaNacimiento(formato.parse(campos[6].trim()));
                    } catch (Exception e) {
                        usuario.setFechaNacimiento(null);
                    }
                }

                usuario.setSexo(campos.length > 7 ? campos[7].trim() : "");
                usuario.setCelular(campos.length > 8 ? campos[8].trim() : "");
                usuario.setTelefono(campos.length > 9 ? campos[9].trim() : "");
                usuario.setCurp(campos.length > 10 ? campos[10].trim() : "");

                RolJPA rol = new RolJPA();
                if (campos.length > 11 && !campos[11].trim().isEmpty()) {
                    try {
                        rol.setIdRol(Integer.parseInt(campos[11].trim()));
                    } catch (Exception e) {
                        rol.setIdRol(null);
                    }
                }
                usuario.setRol(rol);

                usuarios.add(usuario);
            }

        } catch (Exception ex) {
            // ⚠️ Error real de lectura (archivo corrupto)
            return null;
        }

        return usuarios;
    }

    public List<UsuarioJPA> LecturaArchivoXLSX(File archivo) {

        List<UsuarioJPA> usuarios = new ArrayList<>();

        try (InputStream fileInputStream = new FileInputStream(archivo); XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {

            XSSFSheet workSheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            int rowNum = 0;

            for (Row row : workSheet) {
                rowNum++;

                // 👉 SALTAR ENCABEZADO
                if (rowNum == 1) {
                    continue;
                }

                if (row == null) {
                    continue;
                }

                UsuarioJPA usuario = new UsuarioJPA();

                usuario.setNombre(
                        formatter.formatCellValue(
                                row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setApellidoPaterno(
                        formatter.formatCellValue(
                                row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setApellidoMaterno(
                        formatter.formatCellValue(
                                row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setUserName(
                        formatter.formatCellValue(
                                row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setEmail(
                        formatter.formatCellValue(
                                row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setPassword(
                        formatter.formatCellValue(
                                row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                // 👉 FECHA (NUMÉRICA O TEXTO)
                Cell fechaCell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (fechaCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(fechaCell)) {
                    usuario.setFechaNacimiento(fechaCell.getDateCellValue());
                } else if (fechaCell.getCellType() == CellType.STRING
                        && !fechaCell.getStringCellValue().trim().isEmpty()) {
                    usuario.setFechaNacimiento(sdf.parse(fechaCell.getStringCellValue().trim()));
                }

                usuario.setSexo(
                        formatter.formatCellValue(
                                row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setCelular(
                        formatter.formatCellValue(
                                row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setTelefono(
                        formatter.formatCellValue(
                                row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                usuario.setCurp(
                        formatter.formatCellValue(
                                row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        ).trim()
                );

                // 👉 ROL (NUMÉRICO O TEXTO)
                RolJPA rol = new RolJPA();
                Cell rolCell = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                if (rolCell.getCellType() == CellType.NUMERIC) {
                    rol.setIdRol((int) rolCell.getNumericCellValue());
                } else if (rolCell.getCellType() == CellType.STRING
                        && !rolCell.getStringCellValue().trim().isEmpty()) {
                    rol.setIdRol(Integer.parseInt(rolCell.getStringCellValue().trim()));
                }

                usuario.setRol(rol);
                usuarios.add(usuario);
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // 👈 PARA VER ERROR REAL EN CONSOLA
            return null;
        }

        return usuarios;
    }

    public List<ErrorCarga> ValidarDatosArchivo(List<UsuarioJPA> usuarios) {
        List<ErrorCarga> erroresCarga = new ArrayList();

        int lineaError = 0;

        for (UsuarioJPA usuario : usuarios) {
            lineaError++;
            BindingResult bindingResult = validationService.validateObject(usuario);
            List<ObjectError> errors = bindingResult.getAllErrors();

            for (ObjectError error : errors) {
                FieldError fieldError = (FieldError) error;
                ErrorCarga errorCarga = new ErrorCarga();
                errorCarga.campo = fieldError.getField();
                errorCarga.descripcion = fieldError.getDefaultMessage();
                errorCarga.linea = lineaError;
                erroresCarga.add(errorCarga);
            }
        }
        return erroresCarga;
    }

    public List<UsuarioJPA> leerArchivo(String rutaArchivo) {

        File file = new File(rutaArchivo);

        if (!file.exists()) {
            throw new RuntimeException("El archivo no existe: " + rutaArchivo);
        }

        if (rutaArchivo.toLowerCase().endsWith(".txt")) {
            return LecturaArchivoTXT(file);

        } else if (rutaArchivo.toLowerCase().endsWith(".xlsx")) {
            return LecturaArchivoXLSX(file);

        } else {
            throw new RuntimeException("Extensión de archivo no soportada");
        }
    }
}
