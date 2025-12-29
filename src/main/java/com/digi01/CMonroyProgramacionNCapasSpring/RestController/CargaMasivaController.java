package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.UsuarioJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.DTO.ResultLog;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.CargaMasivaService;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.LogService;
import java.awt.image.RescaleOp;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/cargamasiva")
public class CargaMasivaController {

    @Autowired
    private CargaMasivaService cargaMasivaService;

    @Autowired
    private LogService logService;

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @PostMapping("/subir")
    public ResponseEntity<ResultLog> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {

        try {
            String rutaGuardada = cargaMasivaService.guardarArchivo(archivo);

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
            String nombre = fecha + archivo.getOriginalFilename();

            int idLog = logService.agregarRegistro(
                    "SUBIR",
                    nombre,
                    "GUARDADO",
                    null,
                    rutaGuardada,
                    "Archivo almacenado en el servidor"
            );

            ResultLog resultadoValidacion
                    = cargaMasivaService.validarArchivo(rutaGuardada);

            return ResponseEntity.ok(resultadoValidacion);

        } catch (Exception ex) {
            ResultLog error = new ResultLog();
            error.setCorrect(false);
            error.setMensaje("Error guardando archivo: " + ex.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/procesar/{idLog}/{token}")
    public ResponseEntity<Result> procesar(
            @PathVariable int idLog,
            @PathVariable String token) {

        Result result = new Result();

        if (!logService.validarToken(idLog, token)) {
            logService.agregarRegistro(
                    "PROCESAR",
                    "",
                    "ERROR_TOKEN",
                    null,
                    "",
                    "Token Invalido o expirado"
            );

            result.correct = false;
            result.errorMessage = "Token invalido o expirado";
            return ResponseEntity.status(403).body(result);

        }

        try {

            String rutaArchivo = logService.obtenerRutaArchivo(idLog);

            List<UsuarioJPA> usuarios = cargaMasivaService.leerArchivo(rutaArchivo);

            result = usuarioJPADAOImplementation.AddAll(usuarios);

            if (result.correct) {
                logService.agregarRegistro(
                        token,
                        rutaArchivo,
                        token,
                        token,
                        rutaArchivo,
                        token
                );

                return ResponseEntity.ok(result);

            } else {
                logService.agregarRegistro(
                        "PROCESAR",
                        new File(rutaArchivo).getName(),
                        "ERROR_PROCESO",
                        null,
                        rutaArchivo,
                        result.errorMessage
                );
                return ResponseEntity.status(500).body(result);
            }

        } catch (Exception ex) {
            logService.agregarRegistro(
                    "PROCESAR",
                    "",
                    "ERROR_PROCESO",
                    null,
                    "",
                    ex.getLocalizedMessage()
            );

            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;

            return ResponseEntity.status(500).body(result);
        }
    }

}
