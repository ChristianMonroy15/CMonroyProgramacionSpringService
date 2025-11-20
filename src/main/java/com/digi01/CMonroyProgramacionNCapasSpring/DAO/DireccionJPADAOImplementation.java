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
    public Result Update(DireccionJPA direccionJPA, int idDireccion) {
        Result result = new Result();

        try {
            DireccionJPA direccionBD = entityManager.find(DireccionJPA.class, idDireccion);

            if (direccionBD == null) {
                result.correct = false;
                result.errorMessage = "No existe la dirección con id: " + idDireccion;
                result.status = 400;
                return result;
            }

            direccionBD.setCalle(direccionJPA.getCalle());
            direccionBD.setNumeroExterior(direccionJPA.getNumeroExterior());
            direccionBD.setNumeroInterior(direccionJPA.getNumeroInterior());
            direccionBD.ColoniaJPA.setIdColonia(direccionJPA.ColoniaJPA.getIdColonia()); 

            entityManager.merge(direccionBD);

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
