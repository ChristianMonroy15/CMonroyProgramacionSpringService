package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.EstadoJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EstadoJPADAOImplementation implements IEstadoJPA {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result GetByIdPais(int idPais) {
        Result result = new Result();

        try {
            String query = "From EstadoJPA estadoJPA WHERE estadoJPA.PaisJPA.IdPais = :IdPais ORDER BY estadoJPA.Nombre";
            TypedQuery<EstadoJPA> estados = entityManager.createQuery(query, EstadoJPA.class);
            estados.setParameter("IdPais", idPais);

            result.object = estados.getResultList();
            result.correct = true;
            result.status = 200;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return result;
    }

}
