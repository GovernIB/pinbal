/**
 * 
 */
package es.caib.pinbal.webapp.view;

import java.text.SimpleDateFormat;
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

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;

/**
 * Vista per a generar una auditoria en format Excel.
 * 
 * @author Josep Gayà
 */
public class AuditorGenerarExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;



	@Override
	@SuppressWarnings("unchecked")
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=auditoria.xls");
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"auditor.generar.excel.fulla.titol"));
		int filaInicial = 0;
		int columnaInicial = 0;
		List<ConsultaDto> consultes = (List<ConsultaDto>)model.get("consultes");
		if (consultes != null) {
			taulaAuditoriaEntitat(
					consultes,
					null,
					request,
					workbook,
					sheet,
					filaInicial,
					columnaInicial);
		} else {
			Map<EntitatDto, List<ConsultaDto>> consultesPerEntitat  = (Map<EntitatDto, List<ConsultaDto>>)model.get(
					"consultesPerEntitat");
			int filaActual = filaInicial;
			for (EntitatDto entitat: consultesPerEntitat.keySet()) {
				filaActual = taulaAuditoriaEntitat(
						consultesPerEntitat.get(entitat),
						getMessage(request, "auditor.generar.excel.entitat.titol") + ": " + entitat.getNom(),
						request,
						workbook,
						sheet,
						filaActual,
						columnaInicial);
				filaActual += 1;
			}
		}
		autoSize(sheet, 7);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private int taulaAuditoriaEntitat(
			List<ConsultaDto> consultes,
			String entitatTitol,
			HttpServletRequest request,
			HSSFWorkbook workbook,
			HSSFSheet sheet,
			int filaInicial,
			int columnaInicial) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		// Configuració dels estils
		HSSFCellStyle capsaleraEntitatStyle = workbook.createCellStyle();
		capsaleraEntitatStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		capsaleraEntitatStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont entitatFont = workbook.createFont();
		entitatFont.setFontName("Arial");
		entitatFont.setFontHeightInPoints((short)16);
		entitatFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraEntitatStyle.setFont(entitatFont);
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		capsaleraStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// Capsalera d'entitat
		if (entitatTitol != null) {
			HSSFRow capsaleraEntitat = sheet.createRow(filaInicial);
			HSSFCell entitatCell = capsaleraEntitat.createCell(columnaInicial);
			entitatCell.setCellStyle(capsaleraEntitatStyle);
			entitatCell.setCellValue(entitatTitol);
			sheet.addMergedRegion(
					new CellRangeAddress(
							filaInicial,
							filaInicial,
							columnaInicial,
							columnaInicial + 7));
			capsaleraEntitat.setHeight((short)0);
		}
		// Capsalera de la taula
		HSSFRow titolFila = sheet.createRow(filaInicial + 1);
		HSSFCell titolNumPeticioCell = titolFila.createCell(columnaInicial);
		titolNumPeticioCell.setCellStyle(capsaleraStyle);
		titolNumPeticioCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.peticion.id"));
		HSSFCell titolDataCell = titolFila.createCell(columnaInicial + 1);
		titolDataCell.setCellStyle(capsaleraStyle);
		titolDataCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.data"));
		HSSFCell titolUsuariCell = titolFila.createCell(columnaInicial + 2);
		titolUsuariCell.setCellStyle(capsaleraStyle);
		titolUsuariCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.usuari"));
		HSSFCell titolFuncionariNomCell = titolFila.createCell(columnaInicial + 3);
		titolFuncionariNomCell.setCellStyle(capsaleraStyle);
		titolFuncionariNomCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.funcionari.nom"));
		HSSFCell titolFuncionariDocumentCell = titolFila.createCell(columnaInicial + 4);
		titolFuncionariDocumentCell.setCellStyle(capsaleraStyle);
		titolFuncionariDocumentCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.funcionari.nif"));
		HSSFCell titolProcedimentCell = titolFila.createCell(columnaInicial + 5);
		titolProcedimentCell.setCellStyle(capsaleraStyle);
		titolProcedimentCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.procediment"));
		HSSFCell titolServeiCell = titolFila.createCell(columnaInicial + 6);
		titolServeiCell.setCellStyle(capsaleraStyle);
		titolServeiCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.servei"));
		HSSFCell titolEstatCell = titolFila.createCell(columnaInicial + 7);
		titolEstatCell.setCellStyle(capsaleraStyle);
		titolEstatCell.setCellValue(
				getMessage(
						request,
						"auditor.list.taula.estat"));
		// Contigut de la taula
		int rowIndex = 1;
		if (consultes != null) {
			for (ConsultaDto consulta: consultes) {
				HSSFRow fila = sheet.createRow(filaInicial + 1 + rowIndex++);
				HSSFCell numPeticioCell = fila.createCell(columnaInicial);
				numPeticioCell.setCellValue(consulta.getScspPeticionSolicitudId());
				HSSFCell dataCell = fila.createCell(columnaInicial + 1);
				dataCell.setCellValue(sdf.format(consulta.getCreacioData()));
				HSSFCell usuariCell = fila.createCell(columnaInicial + 2);
				usuariCell.setCellValue(consulta.getCreacioUsuari().getNom());
				HSSFCell funcionariNomCell = fila.createCell(columnaInicial + 3);
				funcionariNomCell.setCellValue(consulta.getFuncionariNom());
				HSSFCell funcionariDocumentCell = fila.createCell(columnaInicial + 4);
				funcionariDocumentCell.setCellValue(consulta.getFuncionariNif());
				HSSFCell procedimentCell = fila.createCell(columnaInicial + 5);
				procedimentCell.setCellValue(consulta.getProcedimentNom());
				HSSFCell serveiCell = fila.createCell(columnaInicial + 6);
				serveiCell.setCellValue(consulta.getServeiDescripcio());
				HSSFCell estatCell = fila.createCell(columnaInicial + 7);
				estatCell.setCellValue(consulta.getEstat());
			}
		}
		return filaInicial + 1 + rowIndex;
	}

	private void autoSize(
			HSSFSheet sheet,
			int numCells) {
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

}
