package com.digi01.CMonroyProgramacionNCapasSpring.DAO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsuarioJPADAOImplementation implements IUsuarioJPA {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager; //DataSource

    @Autowired
    private EmailService emailService;

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
            usuarioJPA.setPassword(passwordEncoder.encode(usuarioJPA.getPassword()));
            entityManager.persist(usuarioJPA);
            String token = UUID.randomUUID().toString();
            usuarioJPA.setVerificationToken(token);
            emailService.sendMail(usuarioJPA.getEmail(), token);

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

    @Override
    public Result UpdateImagen(int idUsuario, String base64) {
        Result result = new Result();

        try {

            UsuarioJPA usuarioBD = entityManager.find(UsuarioJPA.class, idUsuario);
            usuarioBD.setImagen(base64);
            result.correct = true;
            result.status = 202;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return result;
    }

    @Override
    public Result UpdateStatus(int idUsuario, int status) {
        Result result = new Result();

        UsuarioJPA usuarioBD = entityManager.find(UsuarioJPA.class, idUsuario);

        if (usuarioBD == null) {
            result.correct = false;
            result.status = 404;
            result.errorMessage = "Usuario no encontrado";
            return result;
        }

        usuarioBD.setStatus(status);

        result.correct = true;
        result.status = 200;
        return result;
    }

    @Transactional
    @Override
    public Result AddAll(List<UsuarioJPA> usuarios) {

        Result result = new Result();

        try {

            for (UsuarioJPA usuario : usuarios) {

                // Relación Direcciones (si aplica)
                if (usuario.getDireccionesJPA() != null && !usuario.getDireccionesJPA().isEmpty()) {
                    usuario.getDireccionesJPA().get(0).UsuarioJPA = usuario;
                }

                // Valores por defecto
                usuario.setStatus(1);

                // Encriptar password
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

                // Generar token de verificación
                String verificationToken = UUID.randomUUID().toString();
                usuario.setVerificationToken(verificationToken);

                // Persistir
                entityManager.persist(usuario);

                // Enviar correo
                emailService.sendMail(usuario.getEmail(), verificationToken);
            }

            result.correct = true;

        } catch (Exception ex) {

            // Si uno falla, se revierte TODO
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;

            // Forzar rollback explícito si lo deseas
            throw ex;
        }

        return result;
    }

}
