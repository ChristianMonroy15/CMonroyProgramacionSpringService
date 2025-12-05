package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.CustomUserDetails;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.JwtService;
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
    public TokenResponse login(@RequestBody LoginRequest request, HttpServletResponse response) throws Exception {

        System.out.println("➡ Intentando autenticación para: " + request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            System.out.println("✔ AUTENTICACIÓN EXITOSA");

            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

            int idUsuario = principal.getIdUsuario(); // debe existir

            String token = jwtService.generateToken(
                    request.getUsername(),
                    roles,
                    idUsuario
            );

            return new TokenResponse(token);

        } catch (Exception ex) {
            System.out.println("❌ ERROR en autenticación: " + ex.getClass().getSimpleName());
            System.out.println("❌ MENSAJE: " + ex.getMessage());
            throw ex;
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
