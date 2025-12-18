package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.DTO.LoginErrorResponse;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.CustomUserDetails;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

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

        } catch (org.springframework.security.authentication.DisabledException ex) {

            // CUENTA NO VERIFICADA
            return ResponseEntity
                    .status(403)
                    .body(new LoginErrorResponse(
                            "La cuenta no está verificada. Revisa tu correo.",
                            "UNVERIFIED_ACCOUNT"
                    ));

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {

            return ResponseEntity
                    .status(401)
                    .body(new LoginErrorResponse(
                            "Usuario o contraseña incorrectos",
                            "BAD_CREDENTIALS"
                    ));
        }
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
