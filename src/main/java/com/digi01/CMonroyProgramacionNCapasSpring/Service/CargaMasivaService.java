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
import org.apache.poi.ss.usermodel.Row;
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
    
    public ResultLog validarArchivo(String rutaArchivo, int idLogSubida) {

    ResultLog resultado = new ResultLog();
    resultado.setIdLog(idLogSubida);

    try {
        File file = new File(rutaArchivo);

        List<UsuarioJPA> usuarios;

        // Detectar extensión
        if (rutaArchivo.toLowerCase().endsWith(".txt")) {
            usuarios = LecturaArchivoTXT(file);
        } else if (rutaArchivo.toLowerCase().endsWith(".xlsx")) {
            usuarios = LecturaArchivoXLSX(file);
        } else {

            logService.agregarRegistro(
                    "VALIDAR",
                    file.getName(),
                    "ERROR_VALIDACION",
                    null,
                    "Extensión no soportada"
            );

            resultado.setCorrect(false);
            resultado.setMensaje("Extensión no soportada.");
            return resultado;
        }

        if (usuarios == null) {

            logService.agregarRegistro(
                    "VALIDAR",
                    file.getName(),
                    "ERROR_VALIDACION",
                    null,
                    "Error al leer el archivo"
            );

            resultado.setCorrect(false);
            resultado.setMensaje("Error al leer el archivo.");
            return resultado;
        }

        // Validar contenido
        List<ErrorCarga> errores = ValidarDatosArchivo(usuarios);

        if (!errores.isEmpty()) {

            logService.agregarRegistro(
                    "VALIDAR",
                    file.getName(),
                    "ERROR_VALIDACION",
                    null,
                    "Se encontraron errores en los datos"
            );

            resultado.setCorrect(false);
            resultado.setMensaje("Se encontraron errores en los datos.");
            resultado.setErrores(errores); //  <<— LOS REGRENAMOS AL CLIENTE
            return resultado;
        }

        // Si todo está correcto → generar token
        String token = UUID.randomUUID().toString();

        logService.agregarRegistro(
                "VALIDAR",
                file.getName(),
                "VALIDADO",
                token,
                "Validación correcta"
        );

        resultado.setCorrect(true);
        resultado.setMensaje("Validación correcta.");
        resultado.setToken(token);
        return resultado;

    } catch (Exception ex) {

        logService.agregarRegistro(
                "VALIDAR",
                new File(rutaArchivo).getName(),
                "ERROR_VALIDACION",
                null,
                "Excepción: " + ex.getMessage()
        );

        resultado.setCorrect(false);
        resultado.setMensaje("Error en validación: " + ex.getMessage());
        return resultado;
    }
}
public List<UsuarioJPA> LecturaArchivoTXT(File archivo) {

        List<UsuarioJPA> usuarios = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(archivo); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            String linea = "";

            while ((linea = bufferedReader.readLine()) != null) {

                String[] campos = linea.split("\\|");
                UsuarioJPA usuario = new UsuarioJPA();

                usuario.setNombre(campos[0].trim());
                usuario.setApellidoPaterno(campos[1].trim());
                usuario.setApellidoMaterno(campos[2].trim());
                usuario.setUserName(campos[3].trim());
                usuario.setEmail(campos[4].trim());
                usuario.setPassword(campos[5].trim());

                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = campos[6].trim();
                Date fecha2 = formato.parse(fecha);

                usuario.setFechaNacimiento(fecha2);

                usuario.setSexo(campos[7].trim());
                usuario.setCelular(campos[8].trim());
                usuario.setTelefono(campos[9].trim());
                usuario.setCurp(campos[10].trim());
                usuario.setRol(new RolJPA());
                usuario.Rol.setIdRol(Integer.parseInt(campos[11].trim()));

                usuarios.add(usuario);
            }

        } catch (Exception ex) {
            return null;
        }
        return usuarios;
    }

    public List<UsuarioJPA> LecturaArchivoXLSX(File archivo) {

        List<UsuarioJPA> usuarios = new ArrayList<>();

        try (InputStream fileInputStream = new FileInputStream(archivo); XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            XSSFSheet workSheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : workSheet) {

                UsuarioJPA usuario = new UsuarioJPA();

                usuario.setNombre(formatter.formatCellValue(
                        row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setApellidoPaterno(formatter.formatCellValue(
                        row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setApellidoMaterno(formatter.formatCellValue(
                        row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );

                usuario.setUserName(formatter.formatCellValue(
                        row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setEmail(formatter.formatCellValue(
                        row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setPassword(formatter.formatCellValue(
                        row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );

                Cell fechaCell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (fechaCell.getCellType() == CellType.NUMERIC) {
                    usuario.setFechaNacimiento(fechaCell.getDateCellValue());
                }

                usuario.setSexo(formatter.formatCellValue(
                        row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setCelular(formatter.formatCellValue(
                        row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setTelefono(formatter.formatCellValue(
                        row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );
                usuario.setCurp(formatter.formatCellValue(
                        row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                );

                usuario.Rol = new RolJPA();
                usuario.Rol.setIdRol(
                        (int) row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                .getNumericCellValue()
                );

                usuarios.add(usuario);

            }

        } catch (Exception ex) {
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
}
