package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.PasswordResetTokenJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Repository.PasswordResetTokenRepository;
import com.digi01.CMonroyProgramacionNCapasSpring.Repository.UserRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void processForgotPassword(String email) {

        Optional<UsuarioJPA> usuario = userRepository.findByEmail(email);

        if (usuario.isPresent()) {

            passwordResetTokenRepository.deleteByEmail(email);

            PasswordResetTokenJPA resetToken = new PasswordResetTokenJPA();
            resetToken.setEmail(email);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpirationDate(
                    LocalDateTime.now(ZoneOffset.UTC).plusMinutes(15));
            resetToken.setUsed(0);

            passwordResetTokenRepository.save(resetToken);

            emailService.sendPasswordResetMail(email, resetToken.getToken());
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetTokenJPA resetToken
                = passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("TOKEN_INVALID"));

        // 1️⃣ Verificar expiración (MISMA ZONA)
        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("TOKEN_EXPIRED");
        }

        // 2️⃣ Verificar si ya fue usado
        if (resetToken.getUsed() == 1) {
            throw new RuntimeException("TOKEN_USED");
        }

        // 3️⃣ Obtener usuario
        UsuarioJPA usuario
                = userRepository.findByEmail(resetToken.getEmail())
                        .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 4️⃣ Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(usuario);

        // 5️⃣ Marcar token como usado
        resetToken.setUsed(1);
        passwordResetTokenRepository.save(resetToken);
    }

}
