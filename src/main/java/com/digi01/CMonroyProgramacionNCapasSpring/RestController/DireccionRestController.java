package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.DireccionJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.DireccionJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class DireccionRestController {

    @Autowired
    private DireccionJPADAOImplementation direccionJPADAOImplementation;

    @GetMapping("direccion/{idDireccion}")
    public ResponseEntity GetById(@PathVariable("idDireccion") int idDireccion) {
        Result result = new Result();

        try {
            result = direccionJPADAOImplementation.GetById(idDireccion);
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

    @PostMapping("direccion/{idUsuario}")
    public ResponseEntity Add(@RequestBody DireccionJPA direccionJPA,
            @PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();

        if (direccionJPA == null) {
            result.correct = false;
            result.status = 400;
        } else {
            try {
                result.object = direccionJPADAOImplementation.Add(direccionJPA, idUsuario);
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

    @DeleteMapping("direccion/{idDireccion}")
    public ResponseEntity Delete(@PathVariable("idDireccion") int idDireccion) {
        Result result = new Result();

        if (idDireccion > 0) {
            try {
                result = direccionJPADAOImplementation.Delete(idDireccion);
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

    @PutMapping("direccion/usuario/{idUsuario}")
    public ResponseEntity Update(@PathVariable("idUsuario") int idUsuario,
            @RequestBody DireccionJPA direccionJPA) {
        Result result = new Result();
        if (direccionJPA.getIdDireccion() > 0) {
            try {
                direccionJPADAOImplementation.Update(direccionJPA, idUsuario);
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

}
