package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import java.util.List;

public interface IUsuarioJPA {

    Result GetAll();

    Result Add(UsuarioJPA usuarioJPA);

    Result GetById(int IdUsuario);

    Result Update(UsuarioJPA usuarioJPA);
    
    Result Delete(int IdUsuario);
    
    Result UpdateImagen(int IdUsuario, String Imagen);
    
    Result GetAllDinamico(UsuarioJPA usuarioJPA);
    
    Result AddAll(List<UsuarioJPA> usuariosJPA);
    
    Result UpdateStatus(int IdUsuario, int Status);

}
