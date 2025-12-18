package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.Service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam("token") String token) {

        boolean isValid = verificationTokenService.validateVerificationToken(token);

        if (!isValid) {
            return ResponseEntity
                    .status(302)
                    .header("Location", "http://localhost:8081/error-verificacion")
                    .build();
        }

        return ResponseEntity
                .status(302)
                .header("Location", "http://localhost:8081/login?verified=true")
                .build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam("username") String username) {

        boolean enviado = verificationTokenService.resendVerification(username);

        if (!enviado) {
            return ResponseEntity
                    .badRequest()
                    .body("No se pudo reenviar el correo de verificación");
        }

        return ResponseEntity.ok("Correo de verificación reenviado");
    }
}
