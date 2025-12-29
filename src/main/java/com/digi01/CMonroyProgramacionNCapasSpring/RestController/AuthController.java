package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DTO.ForgotPasswordRequest;
import com.digi01.CMonroyProgramacionNCapasSpring.DTO.LoginErrorResponse;
import com.digi01.CMonroyProgramacionNCapasSpring.DTO.ResetPasswordRequest;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.PasswordResetTokenJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Repository.PasswordResetTokenRepository;
import com.digi01.CMonroyProgramacionNCapasSpring.Repository.UserRepository;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.CustomUserDetails;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.JwtService;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.EmailService;
import com.digi01.CMonroyProgramacionNCapasSpring.Service.PasswordResetService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    PasswordResetService passwordResetService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Inyección de AuthenticationManager
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws JOSEException {

        System.out.println("➡ Intentando autenticación para: " + request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            System.out.println("✔ AUTENTICACIÓN EXITOSA");

            CustomUserDetails principal
                    = (CustomUserDetails) authentication.getPrincipal();

            if (principal.getStatus() == 0) {
                return ResponseEntity.status(403)
                        .body(new LoginErrorResponse(
                                "Tu cuenta está desactivada. Contacta al administrador.",
                                "USER_DISABLED"
                        ));
            }

            Integer verified = principal.getIsverified();

            if (verified == null || verified == 0) {
                return ResponseEntity.status(403)
                        .body(new LoginErrorResponse(
                                "Debes verificar tu correo antes de iniciar sesión.",
                                "UNVERIFIED_ACCOUNT"
                        ));
            }

            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtService.generateToken(
                    principal.getUsername(),
                    roles,
                    principal.getIdUsuario()
            );

            return ResponseEntity.ok(new TokenResponse(token));

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {

            return ResponseEntity
                    .status(401)
                    .body(new LoginErrorResponse(
                            "Usuario o contraseña incorrectos",
                            "BAD_CREDENTIALS"
                    ));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.processForgotPassword(request.getEmail());

        return ResponseEntity.ok(
                Map.of("message",
                        "Se enviará un enlace de recuperación.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        
        passwordResetService.resetPassword(request.getToken(), request.getPassword());
        
        return ResponseEntity.ok(Map.of(
                "message", "Contraseña actualizada correctamente"
        ));
    }

    @PostConstruct
    public void generate() {
        System.out.println("BCrypt correcto: " + passwordEncoder.encode("qwerty"));
    }

    @Data
    public static class LoginRequest {

        private String username;
        private String password;
    }

    @Data
    public static class TokenResponse {

        private final String token;
    }
}
