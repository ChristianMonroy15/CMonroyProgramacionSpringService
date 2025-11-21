package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.PaisJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class PaisRestController {

    @Autowired
    private PaisJPADAOImplementation paisJPADAOImplementation;

    @GetMapping("pais")
    public ResponseEntity GetAll() {
        Result result = new Result();
        result = paisJPADAOImplementation.GetAll();
        return ResponseEntity.status(result.status).body(result);
    }

}
