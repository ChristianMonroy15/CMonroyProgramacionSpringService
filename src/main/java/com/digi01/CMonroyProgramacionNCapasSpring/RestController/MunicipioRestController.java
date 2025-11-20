
package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.MunicipioJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class MunicipioRestController {
    
    @Autowired
    private MunicipioJPADAOImplementation municipioJPADAOImplementation;
 
    @GetMapping("municipio/{idEstado}")
    public ResponseEntity GetByIdEstado(@PathVariable("idEstado") int idEstado) {
        Result result = new Result();
        result = municipioJPADAOImplementation.GetByIdEstado(idEstado);
        return ResponseEntity.status(result.status).body(result);
    }   
}
