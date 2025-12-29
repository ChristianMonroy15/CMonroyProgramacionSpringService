package com.digi01.CMonroyProgramacionNCapasSpring.Repository;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.PasswordResetTokenJPA;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokenJPA, Long> {

    Optional<PasswordResetTokenJPA> findByToken(String token);

    Optional<PasswordResetTokenJPA> findByTokenAndUsed(String token, Integer used);

    void deleteByEmail(String email);
}
