package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Repository.UserRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void createVerificationToken(UsuarioJPA usuario, String token) {
        usuario.setVerificationToken(token);
        usuario.setIsVerified(0);
        usuario.setStatus(0);
        userRepository.save(usuario);
    }

    public boolean validateVerificationToken(String token) {

        UsuarioJPA usuario = userRepository
                .findByVerificationToken(token)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        usuario.setIsVerified(1);
        usuario.setStatus(1);

        usuario.setVerificationToken(null);

        userRepository.save(usuario);

        return true;
    }

    public boolean resendVerification(String username) {

        UsuarioJPA usuario = userRepository
                .findByUsername(username)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        // Si ya está verificado, no reenviar
        if (usuario.getIsVerified() != null && usuario.getIsVerified() == 1) {
            return false;
        }

        String newToken = UUID.randomUUID().toString();

        usuario.setVerificationToken(newToken);
        userRepository.save(usuario);

        emailService.sendMail(usuario.getEmail(), newToken);

        return true;
    }

}
