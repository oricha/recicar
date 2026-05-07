package com.recicar.marketplace.service.notification;

import com.recicar.marketplace.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional auth emails when {@link JavaMailSender} is configured; otherwise logs locally.
 */
@Service
public class AuthMailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(AuthMailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${app.company.display-name:ReciCar}")
    private String displayName;

    @Value("${app.baseUrl:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.mail.from:noreply@recicar.local}")
    private String fromAddress;

    public AuthMailNotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailVerification(User user, String verifyUrlPathWithQuery) {
        String body = "Hola " + user.getFirstName() + ",\n\n"
                + "Verifica tu correo visitando:\n"
                + baseUrl + verifyUrlPathWithQuery
                + "\n\nEl enlace caduca en 24 horas.\n\n"
                + "— " + displayName;
        send(user.getEmail(), "Verifica tu correo — " + displayName, body);
    }

    public void sendPasswordReset(User user, String resetUrlPathWithQuery) {
        String body = "Hola " + user.getFirstName() + ",\n\n"
                + "Restablece tu contraseña en:\n"
                + baseUrl + resetUrlPathWithQuery
                + "\n\nEl enlace caduca en 1 hora y solo puede usarse una vez.\n\n"
                + "— " + displayName;
        send(user.getEmail(), "Restablecer contraseña — " + displayName, body);
    }

    public void sendPasswordChangedNotice(User user) {
        String body = "Hola " + user.getFirstName() + ",\n\n"
                + "Tu contraseña se ha cambiado correctamente.\n"
                + "Si no fuiste tú, contacta con soporte de inmediato.\n\n"
                + "— " + displayName;
        send(user.getEmail(), "Contraseña actualizada — " + displayName, body);
    }

    public void sendFarewellAnonymized(String email) {
        String body = "Tu solicitud de eliminación de cuenta se ha procesado. Ciertos datos pueden conservarse de forma anonimizada por obligaciones legales (pedidos).\n\n"
                + "— " + displayName;
        send(email, "Cuenta cerrada — " + displayName, body);
    }

    private void send(String to, String subject, String text) {
        if (mailSender != null) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent: subject=\"{}\" to={}", subject, to);
        } else {
            log.info("[MAIL STUB] to={} subject={}\n{}", to, subject, text);
        }
    }
}
