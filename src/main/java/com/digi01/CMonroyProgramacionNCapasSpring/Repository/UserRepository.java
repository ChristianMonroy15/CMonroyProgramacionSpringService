package com.digi01.CMonroyProgramacionNCapasSpring.Repository;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UsuarioJPA, Integer> {

    @Query("SELECT u FROM UsuarioJPA u WHERE u.Email = :email")
    Optional<UsuarioJPA> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UsuarioJPA u WHERE u.userName = :username")
    Optional<UsuarioJPA> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UsuarioJPA u WHERE u.VerificationToken = :token")
    Optional<UsuarioJPA> findByVerificationToken(@Param("token") String token);
}
