
package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.EstadoJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


public class EstadoJPADAOImplementation implements IEstadoJPA{

    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Result GetByIdPais(int idPais) {
        Result result = new Result();
        
        try {
            String query = "From EstadoJPA WHERE IdPais = :IdPais";
            TypedQuery<EstadoJPA> estados = entityManager.createQuery(query, EstadoJPA.class);
            estados.setParameter("IdPais", idPais);
            
            result.objects = (List<Object>) (List<?>) estados.getResultList();
            result.correct = true;
            
            
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
}
