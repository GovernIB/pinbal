package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EmailHelperTest {

    @Mock private JavaMailSender mailSender;
    @Mock private ConfigHelper configHelper;

    private TestEmailHelper emailHelper;

    private static class TestEmailHelper extends EmailHelper {
        TestEmailHelper(JavaMailSender mailSender, ConfigHelper configHelper) {
            super(mailSender, configHelper);
        }

        @Override
        protected String getMailHtmlBody() {
            return "<p>cos html</p>";
        }

        @Override
        protected String getMailPlainTextBody() {
            return "cos text pla";
        }

        @Override
        protected String getMailSubject() {
            return "Assumpte de prova";
        }
    }

    @BeforeEach
    public void setUp() {
        // Necessari perquè InjectMocks no invoca el constructor amb arguments de la classe abstracta
        emailHelper = new TestEmailHelper(mailSender, configHelper);
    }

    private MimeMessage realMimeMessage() {
        return new MimeMessage(Session.getDefaultInstance(new Properties()));
    }

    @Test
    public void sendMail_enviamentCorrecte_retornaNull() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(configHelper.getConfig("es.caib.pinbal.email.remitent")).thenReturn("pinbal@caib.es");
        when(configHelper.getConfig(eq("es.caib.pinbal.email.footer"), anyString())).thenReturn("");

        String result = emailHelper.sendMail(new String[]{"desti@caib.es"});

        assertNull(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void sendMail_errorEnviament_retornaMissatgeErrorSenseLlancarExcepcio() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(configHelper.getConfig("es.caib.pinbal.email.remitent")).thenReturn("pinbal@caib.es");
        when(configHelper.getConfig(eq("es.caib.pinbal.email.footer"), anyString())).thenReturn("");
        doThrow(new RuntimeException("error smtp")).when(mailSender).send(any(MimeMessage.class));

        String result = emailHelper.sendMail(new String[]{"desti@caib.es"});

        assertNotNull(result);
        assertTrue(result.contains("No s'ha pogut avisar per correu electrònic"));
    }

    @Test
    public void getRemitent_delegaAConfigHelper() {
        when(configHelper.getConfig("es.caib.pinbal.email.remitent")).thenReturn("pinbal@caib.es");

        assertEquals("pinbal@caib.es", emailHelper.getRemitent());
    }

    @Test
    public void getEmailFooter_delegaAConfigHelperAmbDefecteBuit() {
        when(configHelper.getConfig("es.caib.pinbal.email.footer", "")).thenReturn("Peu de pàgina");

        assertEquals("Peu de pàgina", emailHelper.getEmailFooter());
    }

    @Test
    public void sendMail_missatgeIncorporaAssumpteAmbPrefixPinbal() throws Exception {
        MimeMessage mimeMessage = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(configHelper.getConfig("es.caib.pinbal.email.remitent")).thenReturn("pinbal@caib.es");
        when(configHelper.getConfig(eq("es.caib.pinbal.email.footer"), anyString())).thenReturn("");

        emailHelper.sendMail(new String[]{"desti@caib.es"});

        assertEquals("[PINBAL] Assumpte de prova", mimeMessage.getSubject());
    }
}
