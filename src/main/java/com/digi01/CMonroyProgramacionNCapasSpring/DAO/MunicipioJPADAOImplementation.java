package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.EstadoJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.MunicipioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class MunicipioJPADAOImplementation implements IMunicipioJPA {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result GetByIdEstado(int idEstado) {
        Result result = new Result();

        try {
            String query = "From EstadoJPA WHERE IdEstado = :IdEstado";
            TypedQuery<MunicipioJPA> municipios = entityManager.createQuery(query, MunicipioJPA.class);
            municipios.setParameter("IdEstado", idEstado);

            result.objects = (List<Object>) (List<?>) municipios.getResultList();
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

}
