
package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;


public interface IColoniaJPA {
    
    Result GetByIdMunicipio(int idMunicipio);
    Result GetByIdCodigoPostal(int codigoPostal);

}
