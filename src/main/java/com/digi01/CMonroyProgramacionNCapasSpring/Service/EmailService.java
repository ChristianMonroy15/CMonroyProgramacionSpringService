/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true para HTML

            helper.setTo(toEmail);
            helper.setSubject("Verificación de Correo");
            String verificationUrl = "http://localhost:8080/api/verify?token=" + token;
            String htmlMsg
                    = "<!DOCTYPE html>"
                    + "<html>"
                    + "<body style='margin:0; padding:0; background-color:#f4f4f4;'>"
                    + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4; padding:20px;'>"
                    + "<tr>"
                    + "<td align='center'>"
                    + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; font-family:Arial, sans-serif;'>"
                    + "<tr>"
                    + "<td style='background-color:#007bff; color:#ffffff; padding:15px; text-align:center;'>"
                    + "<h1 style='margin:0; font-size:22px;'>Verificación de Correo</h1>"
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:20px; text-align:center; color:#333333;'>"
                    + "<p style='font-size:16px;'>Bienvenido,</p>"
                    + "<p style='font-size:15px;'>Para continuar, por favor verifica tu correo electrónico haciendo clic en el botón:</p>"
                    + "<a href='" + verificationUrl + "' "
                    + "style='display:inline-block; margin-top:20px; padding:12px 25px; "
                    + "background-color:#007bff; color:#ffffff; text-decoration:none; "
                    + "font-size:16px; border-radius:4px;'>"
                    + "Verificar Correo"
                    + "</a>"
                    + "<p style='margin-top:20px; font-size:14px; color:#555555;'>"
                    + "Es necesario verificar tu cuenta para poder iniciar sesión."
                    + "</p>"
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:15px; text-align:center; font-size:12px; color:#777777;'>"
                    + "&copy; 2025 Digis01 Soluciones Digitales"
                    + "</td>"
                    + "</tr>"
                    + "</table>"
                    + "</td>"
                    + "</tr>"
                    + "</table>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlMsg, true); // true indica contenido HTML

            mailSender.send(message);
            System.out.println("Correo de verificación enviado a: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
