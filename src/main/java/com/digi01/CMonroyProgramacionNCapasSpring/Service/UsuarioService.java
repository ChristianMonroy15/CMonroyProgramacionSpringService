package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.UsuarioJPADAOImplementation;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @Transactional
    public Result UpdateStatus(int idUsuario, int status) {
        return usuarioJPADAOImplementation.UpdateStatus(idUsuario, status);
    }
    
    @Transactional
    public Result UpdateImagen(int idUsuario, String base64){
        return usuarioJPADAOImplementation.UpdateImagen(idUsuario, base64);
    }
    

}
