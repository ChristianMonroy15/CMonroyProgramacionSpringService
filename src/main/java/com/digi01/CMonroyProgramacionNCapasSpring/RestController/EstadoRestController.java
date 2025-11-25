package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.EstadoJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class EstadoRestController {

    @Autowired
    EstadoJPADAOImplementation estadoJPADAOImplementation;

    @GetMapping("estado/pais/{idPais}")
    public ResponseEntity GetByIdPais(@PathVariable("idPais") int idPais) {

        Result result = new Result();
        result = estadoJPADAOImplementation.GetByIdPais(idPais);
        return ResponseEntity.status(result.status).body(result);

    }

}
