package es.caib.pinbal.core.helper;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@Component
public abstract class EmailHelper {
    private static final String PREFIX_PINBAL = "[PINBAL]";

    @Autowired
	private JavaMailSender mailSender;
    @Autowired
    private ConfigHelper configHelper;

    protected abstract String getMailHtmlBody();
    protected abstract String getMailPlainTextBody();
    protected abstract String getMailSubject();

    public String sendMail(String[] emailDestinataris) throws Exception {
        String resposta = null;
        try {
            sendEmailReport(emailDestinataris);
        } catch (Exception ex) {
            String errorDescripció = "No s'ha pogut avisar per correu electrònic: " + ex;
            log.error(errorDescripció);
            resposta = errorDescripció;
        }
        return resposta;
    }
    protected void sendEmailReport(
            String[] emailDestinataris) throws MessagingException {
    	sendEmailReport(emailDestinataris, null);
    }
    protected void sendEmailReport(
            String[] emailDestinataris, List<Attachment> files) throws MessagingException {
        log.debug("Enviament correu report diari de l'estat de PINBAL");

        MimeMessage missatge = mailSender.createMimeMessage();
        missatge.setHeader("Content-Type", "text/html charset=UTF-8");
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(missatge, true);
        helper.setTo(emailDestinataris);
        helper.setFrom(getRemitent());
        helper.setSubject(PREFIX_PINBAL + " " + getMailSubject());
        helper.setText(getMailPlainTextBody() + getEmailFooter());

        if (files != null) {
            for (Attachment attach: files) {
                helper.addAttachment(attach.filename, new ByteArrayResource(attach.content));
            }
        }
        
        mailSender.send(missatge);
    }

    public String getRemitent() {
        return configHelper.getConfig("es.caib.pinbal.email.remitent");
    }
    
    public String getEmailFooter() {
        return configHelper.getConfig("es.caib.pinbal.email.footer", "");
    }

    @AllArgsConstructor
    protected class Attachment{
        @NonNull String filename;
        @NonNull byte[] content;
    }
}
