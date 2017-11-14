/**
 * 
 */
package es.caib.pinbal.webapp.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiDto;

/**
 * Vista per a generar els fitxers amb format Excel de les estadístiques.
 * 
 * @author Josep Gayà
 */
public class PeticioMultiplePlantillaOdsView extends AbstractOdsView implements MessageSourceAware {

	
	private final static int ROW_TITLE = 0;
	private final static int ROW_HEADER = 1;
	private final static int ROW_PATH = 2;
	
	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildOdsDocument(
			Map<String, Object> model,
			SpreadsheetDocument ods,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ServeiDto servei = (ServeiDto)model.get("servei");
		response.setHeader("Content-Disposition", "Inline; filename=plantilla_" + servei.getCodi() + ".ods");
		String titol = getMessage(
				request,
				"consulta.plantilla.titol",
				new Object[] {servei.getCodi()});
		ods.removeSheet(0);
		Table sheet = ods.appendSheet(titol);
		List<ServeiCampDto> camps = (List<ServeiCampDto>)model.get("campsDadesEspecifiques");
		taulaCamps(	titol,
					servei,
					camps,
					request,
					ods,
					sheet);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void taulaCamps(
			String titol,
			ServeiDto servei,
			List<ServeiCampDto> camps,
			HttpServletRequest request,
			SpreadsheetDocument ods,
			Table sheet) {
		
		int columna = 0;
		Cell cell = null;
		
		// Capsalera
		cell = sheet.getCellByPosition(columna, ROW_TITLE);
		setTitleFormat(cell);
		cell.setStringValue(titol);
		
		// Expedient
		cell = sheet.getCellByPosition(columna, ROW_HEADER);
		setHeaderFormat(cell);
		cell.setStringValue(getMessage(request, "consulta.form.camp.expedient"));
		sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
		cell = sheet.getCellByPosition(columna, ROW_PATH);
		setPathFormat(cell);
		cell.setStringValue("DatosGenericos/Solicitante/IdExpediente");
		
		// Document
		if (servei.isPinbalActiuCampDocument()) {
			// Document tipus
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.document.tipus") + 
					(servei.isPinbalDocumentObligatori() ? " * (" : " (") +
					(servei.isPinbalPermesDocumentTipusCif() ? "CIF " : "") +
					(servei.isPinbalPermesDocumentTipusDni() ? "DNI " : "") +
					(servei.isPinbalPermesDocumentTipusNie() ? "NIE " : "") +
					(servei.isPinbalPermesDocumentTipusNif() ? "NIF " : "") +
					(servei.isPinbalPermesDocumentTipusPas() ? "Passaport " : "") + ")");
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/TipoDocumentacion");
			
			// Document num
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.document.num") + 
					(servei.isPinbalDocumentObligatori() ? " *" : ""));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/Documentacion");
		}
		
		// Nom complet
		if (servei.isPinbalActiuCampNomComplet()) {
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.nomcomplet"));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/NombreCompleto");
		}
		
		// Nom
		if (servei.isPinbalActiuCampNom()) {
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.nom"));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/Nombre");
		}
		
		// 1er llinatge
		if (servei.isPinbalActiuCampLlinatge1()) {
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.llinatge1"));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/Apellido1");
		}
		
		// 2on llinatge
		if (servei.isPinbalActiuCampLlinatge2()) {
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			cell.setStringValue(getMessage(request, "consulta.form.camp.llinatge2"));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue("DatosGenericos/Titular/Apellido2");
		}
		
		// Dades específiques
		for (ServeiCampDto camp: camps) {
			cell = sheet.getCellByPosition(++columna, ROW_HEADER);
			setHeaderFormat(cell);
			if (camp.getEtiqueta() != null)
				cell.setStringValue(camp.getEtiqueta() + (camp.isObligatori() ? " *" : ""));
			else
				cell.setStringValue(camp.getCampNom() + (camp.isObligatori() ? " *" : ""));
			sheet.getColumnByIndex(columna).setUseOptimalWidth(true);
			cell = sheet.getCellByPosition(columna, ROW_PATH);
			setPathFormat(cell);
			cell.setStringValue(camp.getPath());
		}
		
		
		sheet.getCellRangeByPosition(
				0, 
				ROW_TITLE, 
				columna, 
				ROW_TITLE).merge();
	}
	
	private void setTitleFormat(Cell cell) {
		
		cell.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
		cell.setVerticalAlignment(VerticalAlignmentType.MIDDLE);
		cell.setBorders(
				CellBordersType.BOTTOM,
				new Border(Color.BLACK, 1, SupportedLinearMeasure.PT));
		cell.setFont(new Font(
				"Arial",
				FontStyle.BOLD,
				16));
	}
	private void setHeaderFormat(Cell cell) {
		
		cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
		cell.setVerticalAlignment(VerticalAlignmentType.MIDDLE);
		cell.setBorders(
				CellBordersType.ALL_FOUR,
				new Border(Color.BLACK, 0.5, SupportedLinearMeasure.PT));
		cell.setFont(new Font(
				"Arial",
				FontStyle.BOLD,
				10));
	}
	private void setPathFormat(Cell cell) {
		
		cell.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
		cell.setVerticalAlignment(VerticalAlignmentType.TOP);
		cell.setBorders(
				CellBordersType.ALL_FOUR,
				new Border(Color.BLACK, 0.5, SupportedLinearMeasure.PT));
		cell.setFont(new Font(
				"Arial",
				FontStyle.REGULAR,
				10));
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
