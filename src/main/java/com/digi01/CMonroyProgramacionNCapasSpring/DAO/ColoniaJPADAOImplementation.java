package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.ColoniaJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ColoniaJPADAOImplementation implements IColoniaJPA {

    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Result GetByIdMunicipio(int idMunicipio) {
        Result result = new Result();

        try {
            String query = "From EstadoJPA WHERE IdMunicipio = :IdMunicipio";
            TypedQuery<ColoniaJPA> colonias = entityManager.createQuery(query, ColoniaJPA.class);
            colonias.setParameter("IdMunicipio", idMunicipio);

            result.objects = (List<Object>) (List<?>) colonias.getResultList();
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    public Result GetByIdCodigoPostal(int codigoPostal) {
        Result result = new Result();

        try {
            String query = "From EstadoJPA WHERE CodigoPostal = :CodigoPostal";
            TypedQuery<ColoniaJPA> colonias = entityManager.createQuery(query, ColoniaJPA.class);
            colonias.setParameter("CodigoPostal", codigoPostal);

            result.objects = (List<Object>) (List<?>) colonias.getResultList();
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
        
    }

}
