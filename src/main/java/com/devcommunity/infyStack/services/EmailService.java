package com.devcommunity.infyStack.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {

    @Value("${infystack.server.url}")
    private String serverUrl;
    @Value("${infystack.domain.email}")
    private String emailSource;
    private final SesClient sesClient;

    @Autowired
    public EmailService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public void sendEmail(String toEmail, String subject, String bodyHtml){

        SendEmailRequest request = SendEmailRequest.builder()
                .source(emailSource)
                .destination(Destination.builder()
                        .toAddresses(toEmail)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .html(Content.builder().data(bodyHtml).build())
                                .build())
                        .build())
                .build();

        sesClient.sendEmail(request);
    }

    public void sendVerificationEmail(String toEmail, String verificationToken) {

        String verificationUrl = serverUrl + "/auth/verifyEmail?token=" + verificationToken;
        String subject = "Verify your InfyStack account";
        String bodyHtml = getEmailVerificationTemplate(verificationUrl);

        sendEmail(toEmail, subject, bodyHtml);
    }

    public String getEmailVerificationTemplate(String verificationUrl) {
        return "<div style='background-color: transparent'>" +
                "<h3 style='color: #333;'>Welcome to InfyStack!</h3>" +
                "<p>Thanks for your interest in joining us. Please click the button below to verify your email.</p>" +
                "<a href='" + verificationUrl + "' style='display: inline-block; padding: 6px 8px; margin-top: 0.2em; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; cursor: pointer;'>Verify Email</a>" +
                "</div>";
    }
}

