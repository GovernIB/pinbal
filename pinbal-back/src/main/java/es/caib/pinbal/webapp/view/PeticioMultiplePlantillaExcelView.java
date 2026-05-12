/**
 * 
 */
package es.caib.pinbal.webapp.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiDto;

/**
 * Vista per a generar els fitxers amb format Excel de les estadístiques.
 * 
 * @author Josep Gayà
 */
public class PeticioMultiplePlantillaExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;


	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ServeiDto servei = (ServeiDto)model.get("servei");
		response.setHeader("Content-Disposition", "Inline; filename=plantilla_" + servei.getCodi() + ".xls");
		String titol = getMessage(
				request,
				"consulta.plantilla.titol",
				new Object[] {servei.getCodi()}); 
		HSSFSheet sheet = workbook.createSheet(titol);
		List<ServeiCampDto> camps = (List<ServeiCampDto>)model.get("campsDadesEspecifiques");
		taulaCamps(	titol,
					servei,
					camps,
					request,
					workbook,
					sheet);
		autoSize(sheet, 6 + camps.size());
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void taulaCamps(
			String titol,
			ServeiDto servei,
			List<ServeiCampDto> camps,
			HttpServletRequest request,
			HSSFWorkbook workbook,
			HSSFSheet sheet) {
		// Configuració dels estils
		HSSFCellStyle capsaleraTitolStyle = workbook.createCellStyle();
		capsaleraTitolStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		capsaleraTitolStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		capsaleraTitolStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		HSSFFont titolFont = workbook.createFont();
		titolFont.setFontName("Arial");
		titolFont.setFontHeightInPoints((short)16);
		titolFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraTitolStyle.setFont(titolFont);
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		capsaleraStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		capsaleraStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		capsaleraStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		capsaleraStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		capsaleraStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		HSSFFont capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraStyle.setFont(capsaleraFont);
		HSSFCellStyle contingutTextStyle = workbook.createCellStyle();
		contingutTextStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		contingutTextStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		contingutTextStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contingutTextStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contingutTextStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contingutTextStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		HSSFFont contingutFont = workbook.createFont();
		contingutFont.setFontName("Arial");
		contingutFont.setFontHeightInPoints((short)10);
		contingutTextStyle.setFont(contingutFont);
		
		// Capsalera
		int filaInicial = 0;
		int columna = 0;
		HSSFRow capsaleraTitol = sheet.createRow(filaInicial);
		HSSFCell cell = capsaleraTitol.createCell(0);
		cell.setCellStyle(capsaleraTitolStyle);
		cell.setCellValue(titol);
		capsaleraTitol.setHeight((short)0);
		
		// Capsalera de la taula
		HSSFRow capsaleraNom = sheet.createRow(filaInicial + 1);
		HSSFRow capsaleraPath = sheet.createRow(filaInicial + 2);
		
		// Expedient
		HSSFCell cellNom = capsaleraNom.createCell(columna);
		cellNom.setCellStyle(capsaleraStyle);
		cellNom.setCellValue(getMessage(request, "consulta.form.camp.expedient"));
		HSSFCell cellPath = capsaleraPath.createCell(columna);
		cellPath.setCellStyle(contingutTextStyle);
		cellPath.setCellValue("DatosGenericos/Solicitante/IdExpediente");

		// Document
		if (servei.isPinbalActiuCampDocument()) {
			// Document tipus
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.document.tipus") + 
					(servei.isPinbalDocumentObligatori() ? " * (" : " (") +
					(servei.isPinbalPermesDocumentTipusCif() ? "CIF " : "") +
					(servei.isPinbalPermesDocumentTipusDni() ? "DNI " : "") +
					(servei.isPinbalPermesDocumentTipusNie() ? "NIE " : "") +
					(servei.isPinbalPermesDocumentTipusNif() ? "NIF " : "") +
					(servei.isPinbalPermesDocumentTipusPas() ? "Passaport " : "") + ")");
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/TipoDocumentacion");
	
			// Document num
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.document.num") + 
					(servei.isPinbalDocumentObligatori() ? " *" : ""));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/Documentacion");
		}

		// Nom complet
		if (servei.isPinbalActiuCampNomComplet()) {
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.nomcomplet"));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/NombreCompleto");
		}
				
		// Nom
		if (servei.isPinbalActiuCampNom()) {
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.nom"));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/Nombre");
		}

		// 1er llinatge
		if (servei.isPinbalActiuCampLlinatge1()) {
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.llinatge1"));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/Apellido1");
		}

		// 2on llinatge
		if (servei.isPinbalActiuCampLlinatge2()) {
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			cellNom.setCellValue(getMessage(request, "consulta.form.camp.llinatge2"));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue("DatosGenericos/Titular/Apellido2");
		}
		
		// Dades específiques
		for (ServeiCampDto camp: camps) {
			cellNom = capsaleraNom.createCell(++columna);
			cellNom.setCellStyle(capsaleraStyle);
			if (camp.getEtiqueta() != null)
				cellNom.setCellValue(camp.getEtiqueta() + (camp.isObligatori() ? " *" : ""));
			else
				cellNom.setCellValue(camp.getCampNom() + (camp.isObligatori() ? " *" : ""));
			cellPath = capsaleraPath.createCell(columna);
			cellPath.setCellStyle(contingutTextStyle);
			cellPath.setCellValue(camp.getPath());
		}
		
		sheet.addMergedRegion(
				new CellRangeAddress(
						filaInicial,
						filaInicial,
						0,
						columna));
	}

	private void autoSize(
			HSSFSheet sheet,
			int numCells) {
//		sheet.setColumnWidth(0, 12000);
//		sheet.setColumnWidth(1, 18000);
		for (int colNum = 0; colNum <= numCells; colNum++)
			sheet.autoSizeColumn(colNum);
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
