package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailReportEstatHelperTest {

    @Mock private JavaMailSender mailSender;
    @Mock private ConfigHelper configHelper;

    @InjectMocks
    private EmailReportEstatHelper emailReportEstatHelper;

    @Test
    public void getMailSubject_retornaTextCorrecte() {
        assertEquals("Report diari de l'estat de PINBAL", emailReportEstatHelper.getMailSubject());
    }

    @Test
    public void getMailPlainTextBody_retornaTextCorrecte() {
        assertNotNull(emailReportEstatHelper.getMailPlainTextBody());
        assertTrue(emailReportEstatHelper.getMailPlainTextBody().contains("PINBAL"));
    }

    @Test
    public void getMailHtmlBody_retornaNull() {
        assertNull(emailReportEstatHelper.getMailHtmlBody());
    }

    @Test
    public void getRemitent_cridaConfigHelper() {
        when(configHelper.getConfig("es.caib.pinbal.email.remitent")).thenReturn("pinbal@caib.es");
        assertEquals("pinbal@caib.es", emailReportEstatHelper.getRemitent());
    }

    @Test
    public void getEmailFooter_cridaConfigHelper() {
        when(configHelper.getConfig("es.caib.pinbal.email.footer", "")).thenReturn("Footer text");
        assertEquals("Footer text", emailReportEstatHelper.getEmailFooter());
    }

    @Test
    public void sendMail_llistaVuida_noEnvia() throws Exception {
        String result = emailReportEstatHelper.sendMail(new String[0]);
        verify(mailSender, never()).send(any(javax.mail.internet.MimeMessage.class));
    }

    @Test
    public void sendMailAmbFitxer_llistaVuida_noEnvia() {
        emailReportEstatHelper.sendMail(new String[0], new byte[]{1, 2, 3});
        verify(mailSender, never()).send(any(javax.mail.internet.MimeMessage.class));
    }
}
