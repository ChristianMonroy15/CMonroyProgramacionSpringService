package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.ColoniaJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ColoniaRestController {

    @Autowired
    private ColoniaJPADAOImplementation coloniaJPADAOImplementation;

    @GetMapping("colonia/municipio/{idMunicipio}")
    public ResponseEntity GetByIdMunicipio(@PathVariable("idMunicipio") int idMunicipio) {
        Result result = new Result();
        result = coloniaJPADAOImplementation.GetByIdMunicipio(idMunicipio);
        return ResponseEntity.status(result.status).body(result);
    }
    
    @GetMapping("colonia/cp/{codigoPostal}")
    public ResponseEntity GetByIdCodigoPostal(@PathVariable("codigoPostal") int codigoPostal){
        Result result = new Result();
        result = coloniaJPADAOImplementation.GetByIdCodigoPostal(codigoPostal);
        return ResponseEntity.status(result.status).body(result);
    }

}
