package es.caib.pinbal.logic.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class EmailReportEstatHelper extends EmailHelper {

	public EmailReportEstatHelper(JavaMailSender mailSender, ConfigHelper configHelper) {
		super(mailSender, configHelper);
	}

	public void sendMail(String[] emailsDestinataris, byte[] fileReportEstatExcel) {
		
		log.debug("Enviant correu report diari de l'estat de PINBAL");
		
		if (emailsDestinataris.length > 0) {

			try {
				sendEmailReport(emailsDestinataris, Arrays.asList(new Attachment("reportEstat.xls", fileReportEstatExcel)));
			} catch (Exception ex) {
				log.error("No s'ha pogut avisar per correu electrònic: " + ex);
			}
		}
	}
		

	@Override
	protected String getMailHtmlBody() {
		return null;
	}

	@Override
	protected String getMailPlainTextBody() {
		return "Adjuntam amb aquest correu l'informe diari d'estat de PINBAL.\n\n";
	}

	@Override
	protected String getMailSubject() {
		return "Report diari de l'estat de PINBAL";
	}
}
