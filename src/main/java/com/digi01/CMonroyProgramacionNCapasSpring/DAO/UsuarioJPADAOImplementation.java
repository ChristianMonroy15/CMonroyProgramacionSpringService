package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsuarioJPADAOImplementation implements IUsuarioJPA {

    @Autowired
    private EntityManager entityManager; //DataSource

    @Override
    public Result GetAll() {
        Result result = new Result();

        try {

            TypedQuery<UsuarioJPA> queryUsuario = entityManager.createQuery("FROM UsuarioJPA", UsuarioJPA.class);
            List<UsuarioJPA> usuarios = queryUsuario.getResultList();

            result.object = usuarios;
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.objects = null;
        }

        return result;
    }

    @Transactional
    @Override
    public Result Add(UsuarioJPA usuarioJPA) {
        Result result = new Result();
        try {
            usuarioJPA.DireccionesJPA.get(0).UsuarioJPA = usuarioJPA;
            usuarioJPA.setStatus(1);
            entityManager.persist(usuarioJPA);
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    public Result GetById(int IdUsuario) {
        Result result = new Result();

        try {

            UsuarioJPA usuarioJPA = entityManager.find(UsuarioJPA.class, IdUsuario);
//          usuarioJPA.DireccionesJPA = new ArrayList<>(usuarioJPA.DireccionesJPA);

            result.object = usuarioJPA;

        } catch (Exception ex) {

            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;

        }
        return result;
    }

    @Transactional
    @Override
    public Result Update(UsuarioJPA usuarioJPA) {
        Result result = new Result();
        try {

            entityManager.merge(usuarioJPA);
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
    public Result Delete(int IdUsuario) {
        Result result = new Result();

        try {

            UsuarioJPA usuarioJPA = entityManager.find(UsuarioJPA.class, IdUsuario);
            entityManager.remove(usuarioJPA);
            result.correct = true;
        } catch (Exception ex) {

            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;

        }
        return result;
    }

    @Override
    public Result GetAllDinamico(UsuarioJPA usuarioJPA) {
        Result result = new Result();

        try {

            String queryDinamica = "FROM UsuarioJPA usuarioJPA WHERE ";
            queryDinamica = queryDinamica + "LOWER(usuarioJPA.Nombre) LIKE LOWER(:Nombre) ";
            queryDinamica = queryDinamica + "AND LOWER(usuarioJPA.ApellidoPaterno) LIKE LOWER(:ApellidoPaterno) ";
            queryDinamica = queryDinamica + "AND LOWER(usuarioJPA.ApellidoMaterno) LIKE LOWER(:ApellidoMaterno) ";

            if (usuarioJPA.Rol != null && usuarioJPA.Rol.getIdRol() > 0) {
                queryDinamica += "AND usuarioJPA.Rol.IdRol = :IdRol ";
            }

            if (usuarioJPA.getStatus() != null && (usuarioJPA.getStatus() == 0 || usuarioJPA.getStatus() == 1)) {
                queryDinamica += "AND usuarioJPA.Status = :Status ";
            }

            queryDinamica = queryDinamica + "ORDER BY usuarioJPA.IdUsuario";

            TypedQuery<UsuarioJPA> query = entityManager.createQuery(queryDinamica, UsuarioJPA.class);

            query.setParameter("Nombre", "%" + usuarioJPA.getNombre() + "%");
            query.setParameter("ApellidoPaterno", "%" + usuarioJPA.getApellidoPaterno() + "%");
            query.setParameter("ApellidoMaterno", "%" + usuarioJPA.getApellidoMaterno() + "%");

            if (usuarioJPA.Rol != null && usuarioJPA.Rol.getIdRol() > 0) {
                query.setParameter("IdRol", usuarioJPA.Rol.getIdRol());
            }

            if (usuarioJPA.getStatus() != null && (usuarioJPA.getStatus() == 0 || usuarioJPA.getStatus() == 1)) {
                query.setParameter("Status", usuarioJPA.getStatus());
            }

            result.object = query.getResultList();
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
    public Result UpdateImagen(int idUsuario, String base64) {
        Result result = new Result();

        try {

            UsuarioJPA usuarioBD = entityManager.find(UsuarioJPA.class, idUsuario);
            usuarioBD.setImagen(base64);
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
    public Result UpdateStatus(int idUsuario, int status) {
        Result result = new Result();

        try {

            UsuarioJPA usuarioBD = entityManager.find(UsuarioJPA.class, idUsuario);

            if (usuarioBD == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
                result.status = 400;
            }

            usuarioBD.setStatus(status);
            result.correct = true;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

    @Override
    public Result AddAll(List<UsuarioJPA> usuariosJPA) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
