/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import jakarta.jms.Queue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private final JmsTemplate jmsTemplate;
    private final Queue emailVerificationQueue;

    public EmailProducer(JmsTemplate jmsTemplate, Queue emailVerificationQueue) {
        this.jmsTemplate = jmsTemplate;
        this.emailVerificationQueue = emailVerificationQueue;
    }

    public void sendVerificationEmail(String emailAddress, String verificationToken) {
        String message = emailAddress + ":" + verificationToken;
        jmsTemplate.convertAndSend(emailVerificationQueue, message);
        System.out.println("Mensaje enviado a la cola para: " + emailAddress);
    }
}
