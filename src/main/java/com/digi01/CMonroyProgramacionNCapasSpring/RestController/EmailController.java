/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.Service.EmailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailProducer emailProducer;
    // Asume que tienes un servicio de usuario para manejar el registro y token

    @Autowired
    public EmailController(EmailProducer emailProducer) {
        this.emailProducer = emailProducer;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email) {
        // Lógica para registrar usuario y generar token (guárdalo en BD)
        String generatedToken = "some-unique-token"; 

        // Envía la tarea a la cola JMS
        emailProducer.sendVerificationEmail(email, generatedToken);

        return "Usuario registrado. Se ha enviado un correo de verificación (asíncrono).";
    }

    // Debes tener otro endpoint para manejar la verificación cuando el usuario hace clic en el enlace
    // @GetMapping("/api/verify") ...
}

