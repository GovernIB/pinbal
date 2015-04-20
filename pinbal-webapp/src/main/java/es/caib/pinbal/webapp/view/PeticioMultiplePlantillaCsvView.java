/**
 * 
 */
package es.caib.pinbal.webapp.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.supercsv.io.ICsvListWriter;

import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiDto;

/**
 * Vista per a generar els fitxers amb format Excel de les estadístiques.
 * 
 * @author Josep Gayà
 */
public class PeticioMultiplePlantillaCsvView extends AbstractCsvView implements MessageSourceAware {

	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildCsvDocument(
			Map<String, Object> model,
			ICsvListWriter csvWriter,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ServeiDto servei = (ServeiDto)model.get("servei");
		response.setHeader("Content-Disposition", "attachment; filename=plantilla_" + servei.getCodi() + ".csv");
		String titol = getMessage(
				request,
				"consulta.plantilla.titol",
				new Object[] {servei.getCodi()}); 
		List<ServeiCampDto> camps = (List<ServeiCampDto>)model.get("campsDadesEspecifiques");
		taulaCamps(	titol,
					servei,
					camps,
					request,
					csvWriter);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void taulaCamps(
			String titol,
			ServeiDto servei,
			List<ServeiCampDto> camps,
			HttpServletRequest request,
			ICsvListWriter csvWriter) throws IOException {
		
		// Titol
		csvWriter.writeHeader(titol);
		// Capsalera de la taula
		List<String> noms = new ArrayList<String>();
		List<String> paths = new ArrayList<String>();
		
		// Expedient
		noms.add(getMessage(request, "consulta.form.camp.expedient"));
		paths.add("DatosGenericos/Solicitante/IdExpediente");

		// Document
		if (servei.isPinbalActiuCampDocument()) {
			// Document tipus
			noms.add(getMessage(request, "consulta.form.camp.document.tipus") + 
					(servei.isPinbalDocumentObligatori() ? " * (" : " (") +
					(servei.isPinbalPermesDocumentTipusCif() ? "CIF " : "") +
					(servei.isPinbalPermesDocumentTipusDni() ? "DNI " : "") +
					(servei.isPinbalPermesDocumentTipusNie() ? "NIE " : "") +
					(servei.isPinbalPermesDocumentTipusNif() ? "NIF " : "") +
					(servei.isPinbalPermesDocumentTipusPas() ? "Passaport " : "") + ")");
			paths.add("DatosGenericos/Titular/TipoDocumentacion");
	
			// Document num
			noms.add(getMessage(request, "consulta.form.camp.document.num") + 
					(servei.isPinbalDocumentObligatori() ? " *" : ""));
			paths.add("DatosGenericos/Titular/Documentacion");
		}

		// Nom complet
		if (servei.isPinbalActiuCampNomComplet()) {
			noms.add(getMessage(request, "consulta.form.camp.nomcomplet"));
			paths.add("DatosGenericos/Titular/NombreCompleto");
		}
		
		// Nom
		if (servei.isPinbalActiuCampNom()) {
			noms.add(getMessage(request, "consulta.form.camp.nom"));
			paths.add("DatosGenericos/Titular/Nombre");
		}

		// 1er llinatge
		if (servei.isPinbalActiuCampLlinatge1()) {
			noms.add(getMessage(request, "consulta.form.camp.llinatge1"));
			paths.add("DatosGenericos/Titular/Apellido1");
		}

		// 2on llinatge
		if (servei.isPinbalActiuCampLlinatge2()) {
			noms.add(getMessage(request, "consulta.form.camp.llinatge2"));
			paths.add("DatosGenericos/Titular/Apellido2");
		}
		
		// Dades específiques
		for (ServeiCampDto camp: camps) {
			if (camp.getEtiqueta() != null)
				noms.add(camp.getEtiqueta() + (camp.isObligatori() ? " *" : ""));
			else
				noms.add(camp.getCampNom() + (camp.isObligatori() ? " *" : ""));
			paths.add(camp.getPath());
		}
		
		csvWriter.write(noms);
		csvWriter.write(paths);
	}

	private String getMessage(
			HttpServletRequest request,
			String key) {
		String message = messageSource.getMessage(
				key,
				null,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	
	private String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
}
