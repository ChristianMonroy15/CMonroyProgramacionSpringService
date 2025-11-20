
package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.DireccionJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;


public interface IDireccionJPA {
    
    Result Add (DireccionJPA direccionJPA, int IdUsuario);
    Result Update(DireccionJPA direccionJPA, int IdUsuario);
    Result Delete (int IdDireccion);
    Result GetById (int IdDireccion);
    
    
    
}
