package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DTO.ResultLog;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.CargaMasivaService;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.LogService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @PostMapping("/subir")
    public ResponseEntity<ResultLog> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {

        ResultLog result = new ResultLog();

        try {
            String rutaGuardada = cargaMasivaService.guardarArchivo(archivo);

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
            String nombre = fecha + archivo.getOriginalFilename();

            int idLog = logService.agregarRegistro(
                    "SUBIR",
                    nombre,
                    "GUARDADO",
                    null,
                    "Archivo almacenado en el servidor: " + rutaGuardada
            );

            ResultLog validacion = cargaMasivaService.validarArchivo(rutaGuardada, idLog);

            validacion.setMensaje("Archivo subido correctamente. " + validacion.getMensaje());

            return ResponseEntity.ok(validacion);

        } catch (Exception ex) {
            result.setCorrect(false);
            result.setMensaje("Error guardando archivo: " + ex.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
