package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.UsuarioJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("api")
public class UsuarioRestController {

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @GetMapping("usuario")
    public ResponseEntity GetAll() {
        Result result = new Result();

        try {
            result.object = usuarioJPADAOImplementation.GetAll();
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
    public ResponseEntity GetById(@PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();

        if (idUsuario > 0) {
            try {

                result.object = usuarioJPADAOImplementation.GetById(idUsuario);
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

                usuarioUpdate.setPassword(usuarioDB.getPassword());

                usuarioUpdate.setIdUsuario(idUsuario);

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

    @PatchMapping("usuario/imagen/{idUsuario}")
    public ResponseEntity UpdateImagen(@RequestParam("imagen") MultipartFile file,
            @PathVariable("idUsuario") int idUsuario) {

        Result result = new Result();

        if (file == null) {

            result.correct = false;
            result.status = 400;

        } else {

            try {
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                result = usuarioJPADAOImplementation.UpdateImagen(idUsuario, base64);
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

}
