package es.caib.pinbal.core.helper;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailReportEstatHelper extends EmailHelper {
	
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
