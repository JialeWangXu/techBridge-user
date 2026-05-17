package es.techbridge.techbridgeuser.services;

import es.techbridge.techbridgeuser.services.exceptions.MailingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Log4j2
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String publicBaseUrl;

    public MailService(JavaMailSender mailSender,
                       TemplateEngine templateEngine,
                       @Value("${app.public-base-url:http://localhost:8081}") String publicBaseUrl) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Async
    public void sendActivationEmail(String emailTo, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailTo);
            helper.setSubject("Activar cuenta en TechBridge");

            Context context = new Context();
            context.setVariable("activationLink", activationLink);
            context.setVariable("logoUrl", publicBaseUrl + "/images/logo-clean.png");
            String htmlContent = templateEngine.process("activation-email", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            String errorMessage = "No se pudo enviar el email de activación a "+emailTo;
            log.error(errorMessage, e);
            throw new MailingException(errorMessage);
        }
    }

    @Async
    public void sendForgetPasswordEmail(String emailTo, String recoverLink){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailTo);
            helper.setSubject("Recuperación de cuenta TechBridge");

            Context context = new Context();
            context.setVariable("recoverLink", recoverLink);
            context.setVariable("logoUrl", publicBaseUrl + "/images/logo-clean.png");
            String htmlContent = templateEngine.process("recover-email", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            String errorMessage = "No se pudo enviar el email de recuperación de cuenta a "+emailTo;
            log.error(errorMessage, e);
            throw new MailingException(errorMessage);
        }
    }
}
