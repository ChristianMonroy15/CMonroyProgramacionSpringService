
package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IUsuarioRepositoryDAO extends JpaRepository<UsuarioJPA, Integer>{
    
    UsuarioJPA findByUserName(String userName);
    
}
