package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.IUsuarioRepositoryDAO;
import com.digi01.CMonroyProgramacionNCapasSpring.DAO.UsuarioJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.ErrorCarga;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.RolJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.LogService;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.ValidationService;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("api")
public class UsuarioRestController {

    @Autowired
    private IUsuarioRepositoryDAO iUsuarioRepositoryDAO;

    @Autowired
    private LogService logService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @GetMapping("usuario")
    public ResponseEntity GetAll() {
        Result result = new Result();

        try {
            result = usuarioJPADAOImplementation.GetAll();
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("usuario/{idUsuario}")
    public ResponseEntity GetById(
            @PathVariable("idUsuario") int idUsuario,
            Authentication authentication) {

        Result result = new Result();

        try {
            String username = authentication.getName();

            UsuarioJPA loggedUser = iUsuarioRepositoryDAO.findByUserName(username);

            String role = loggedUser.getRol().getNombre();

            if (role.equals("User") && loggedUser.getIdUsuario() != idUsuario) {
                return ResponseEntity.status(403).body("No autorizado");
            }

            if (idUsuario > 0) {

                result = usuarioJPADAOImplementation.GetById(idUsuario);
                result.correct = true;
                result.status = 200;

            } else {
                result.correct = false;
                result.status = 400;
            }

        } catch (Exception ex) {

            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("usuario")
    public ResponseEntity Add(@RequestBody UsuarioJPA usuarioJPA) {

        Result result = new Result();

        if (usuarioJPA == null) {
            result.correct = false;
            result.status = 400;
        } else {
            try {
                result.object = usuarioJPADAOImplementation.Add(usuarioJPA);
                result.correct = true;
                result.status = 201;
            } catch (Exception ex) {
                result.correct = false;
                result.errorMessage = ex.getLocalizedMessage();
                result.ex = ex;
                result.status = 500;
            }
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PutMapping("usuario/{idUsuario}")
    public ResponseEntity Update(@PathVariable("idUsuario") int idUsuario,
            @RequestBody UsuarioJPA usuarioJPA) {

        Result result = new Result();
        if (idUsuario > 0) {
            try {
                Result resultUsuario = usuarioJPADAOImplementation.GetById(idUsuario);

                UsuarioJPA usuarioDB = (UsuarioJPA) resultUsuario.object;

                UsuarioJPA usuarioUpdate = usuarioJPA;
                
                usuarioUpdate.setImagen(usuarioDB.getImagen());

                usuarioUpdate.setPassword(usuarioDB.getPassword());

                usuarioUpdate.setIdUsuario(idUsuario);

                usuarioUpdate.setDireccionesJPA(usuarioDB.getDireccionesJPA());

                usuarioJPADAOImplementation.Update(usuarioUpdate);
                result.correct = true;
                result.status = 200;

            } catch (Exception ex) {
                result.correct = false;
                result.errorMessage = ex.getLocalizedMessage();
                result.ex = ex;
                result.status = 500;
            }

        } else {

            result.correct = false;
            result.status = 400;

        }

        return ResponseEntity.status(result.status).body(result);
    }

    @DeleteMapping("usuario/{idUsuario}")
    public ResponseEntity Delete(@PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();

        if (idUsuario > 0) {

            try {

                result = usuarioJPADAOImplementation.Delete(idUsuario);
                result.correct = true;
                result.status = 200;

            } catch (Exception ex) {

                result.correct = false;
                result.errorMessage = ex.getLocalizedMessage();
                result.ex = ex;
                result.status = 404;

            }

        } else {

            result.correct = false;
            result.status = 400;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @PatchMapping("usuario/imagen/update/{idUsuario}")
    public ResponseEntity UpdateImagen(@RequestParam("imagen") String imagenBase64,
            @PathVariable("idUsuario") int idUsuario) {

        Result result = new Result();

        if (imagenBase64 == null) {

            result.correct = false;
            result.status = 400;

        } else {

            try {
                if (imagenBase64.startsWith("data:image")) {
                    imagenBase64 = imagenBase64.substring(imagenBase64.indexOf(",") + 1);
                }
                result = usuarioJPADAOImplementation.UpdateImagen(idUsuario, imagenBase64);
                result.correct = true;
                result.status = 202;

            } catch (Exception ex) {

                result.correct = false;
                result.errorMessage = ex.getLocalizedMessage();
                result.ex = ex;
                result.status = 500;

            }

        }

        return ResponseEntity.status(result.status).body(result);
    }

    @PatchMapping("usuario/update/status/{idUsuario}")
    public ResponseEntity UpdateStatus(
            @RequestParam("status") int status,
            @PathVariable("idUsuario") int idUsuario) {

        Result result = new Result();

        try {
            result = usuarioJPADAOImplementation.UpdateStatus(idUsuario, status);

            if (result.correct) {
                result.status = 202;
            } else {
                result.status = 400;
            }

        } catch (Exception ex) {

            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("usuario/busqueda")
    public ResponseEntity GetAllDinamico(@RequestBody UsuarioJPA usuarioJPA) {
        Result result = new Result();
        if (usuarioJPA == null) {

            result = usuarioJPADAOImplementation.GetAll();

        } else {
            try {
                result = usuarioJPADAOImplementation.GetAllDinamico(usuarioJPA);
                result.correct = true;
                result.status = 200;
            } catch (Exception ex) {
                result.correct = false;
                result.errorMessage = ex.getLocalizedMessage();
                result.ex = ex;
                result.status = 500;
            }
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("/cargamasiva/procesar")
    public String CargaMasiva(HttpSession session, RedirectAttributes redirectAttributes) {
        String path = session.getAttribute("archivoCargaMasiva").toString();
        session.removeAttribute("archivoCargaMasiva");

        List<UsuarioJPA> usuarios;
        if (path.endsWith(".txt")) {
            usuarios = LecturaArchivoTXT(new File(path));
        } else if (path.endsWith(".xlsx")) {
            usuarios = LecturaArchivoXLSX(new File(path));
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Extensión de archivo no soportada.");
            return "redirect:/usuario/cargamasiva";
        }
        //inserción con carga masiva
        // 
        Result result = usuarioJPADAOImplementation.AddAll(usuarios);
        if (result.correct == true) {
            redirectAttributes.addFlashAttribute("successMessage", "Carga masiva completada exitosamente. Los usuarios fueron insertados en la base de datos.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al insertar los usuarios: " + result.ex.getLocalizedMessage());
        }

        return "redirect:/usuario/cargamasiva";
    }

    @PostMapping("/cargamasiva")
    public String CargaMasiva(@RequestParam("archivo") MultipartFile archivo,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes seleccionar un archivo antes de subirlo.");
        }

        String extension = archivo.getOriginalFilename().split("\\.")[1];

        String path = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivosCarga";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
        String pathDefinitvo = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();

        try {
            archivo.transferTo(new File(pathDefinitvo));

        } catch (Exception ex) {
            String errortransferencia = ex.getLocalizedMessage();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al subir el archivo: " + ex.getMessage());
            return "redirect:usuario/cargamasiva";
        }

        List<UsuarioJPA> usuarios;
        if (extension.equalsIgnoreCase("txt")) {
            usuarios = LecturaArchivoTXT(new File(pathDefinitvo));

        } else if (extension.equalsIgnoreCase("xlsx")) {
            usuarios = LecturaArchivoXLSX(new File(pathDefinitvo));

        } else {
            // error de archivo
            redirectAttributes.addFlashAttribute("errorMessage", "Formato de archivo no soportado.");
            return "redirect:/usuario/cargamasiva";
        }
        List<ErrorCarga> errores = ValidarDatosArchivo(usuarios);
        if (errores.isEmpty()) {
            //Boton de procesar
            model.addAttribute("error", false);
            session.setAttribute("archivoCargaMasiva", pathDefinitvo);
            redirectAttributes.addFlashAttribute("successMessage", "Archivo validado correctamente. Puedes procesarlo.");

        } else {
            //Lista Errores
            model.addAttribute("error", true);
            model.addAttribute("errores", errores);
            redirectAttributes.addFlashAttribute("errorMessage", "Se encontraron errores en el archivo.");

        }
        return "CargaMasiva";
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
