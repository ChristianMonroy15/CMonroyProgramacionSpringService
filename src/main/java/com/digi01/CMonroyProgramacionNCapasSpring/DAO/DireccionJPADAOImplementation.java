package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.DireccionJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DireccionJPADAOImplementation implements IDireccionJPA {

    @Autowired
    private EntityManager entityManager; //DataSource

    @Transactional
    @Override
    public Result Add(DireccionJPA direccionJPA, int IdUsuario) {
        Result result = new Result();

        try {

            UsuarioJPA usuario = entityManager.find(UsuarioJPA.class, IdUsuario);
            direccionJPA.setUsuarioJPA(usuario);

            entityManager.persist(direccionJPA);
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

    @Transactional
    @Override
    public Result Update(DireccionJPA direccionJPA, int idUsuario) {
        Result result = new Result();

        try {
            DireccionJPA direccionBD = entityManager.find(DireccionJPA.class,direccionJPA.getIdDireccion());

            if (direccionBD == null) {
                result.correct = false;
                result.errorMessage = "No existe la dirección indicada.";
                result.status = 400;
                return result;
            }
            if (direccionBD.UsuarioJPA.getIdUsuario() == idUsuario) {
                direccionJPA.UsuarioJPA = direccionBD.UsuarioJPA;
                entityManager.merge(direccionJPA);
                result.correct = true;
                result.status = 200;
            } else {
                result.correct = false;
                result.errorMessage = "No puede ser procesada la operacion.";
                result.status = 404;

            }

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

    @Transactional
    @Override
    public Result Delete(int IdDireccion) {
        Result result = new Result();

        try {

            DireccionJPA direccionJPA = entityManager.find(DireccionJPA.class, IdDireccion);
            entityManager.remove(direccionJPA);
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

    @Override
    public Result GetById(int IdDireccion) {
        Result result = new Result();

        try {

            DireccionJPA direccionJPA = entityManager.find(DireccionJPA.class, IdDireccion);
            result.object = direccionJPA;
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

}
