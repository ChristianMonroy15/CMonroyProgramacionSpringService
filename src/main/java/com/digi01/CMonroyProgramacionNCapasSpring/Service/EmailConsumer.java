/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class EmailConsumer {

    private final EmailService emailService;

    @Autowired
    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @JmsListener(destination = "emailVerificationQueue")
    public void receiveMessage(String message) {
        System.out.println("Mensaje recibido: " + message);
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String userEmail = parts[0];
            String verificationToken = parts[1];
            emailService.sendMail(userEmail, verificationToken); // Llama al servicio de correo real
        }
    }
}
