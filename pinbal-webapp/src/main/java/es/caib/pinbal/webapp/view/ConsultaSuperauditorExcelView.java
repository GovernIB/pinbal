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
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;

/**
 * Vista per a generar els fitxers amb format Excel de les estadístiques.
 * 
 * @author Josep Gayà
 */
public class ConsultaSuperauditorExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=consultes.xls");
		
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)model.get("consultaFiltreCommand");
		
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"consulta.excel.fulla.titol"));
		int filaInicial = 0;
		int columnaInicial = 0;
		List<ConsultaDto> consulta = (List<ConsultaDto>)model.get("consultaList");
		if (consulta != null) {
			taulaConsulta(
					consulta,
					command,					
					request,
					workbook,
					sheet,
					filaInicial,
					columnaInicial);
		}
		
		//Auto size all the columns
	    for (int x = 0; x < 8; x++) {
	    	sheet.autoSizeColumn(x);
	    }
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private int taulaConsulta(
			List<ConsultaDto> consultes,
			ConsultaFiltreCommand command,			
			HttpServletRequest request,
			HSSFWorkbook workbook,
			HSSFSheet sheet,
			int filaInicial,
			int columnaInicial) {
		
		// Configuració dels estils
		HSSFCellStyle titolStyle = workbook.createCellStyle();
		titolStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		titolStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont titolFont = workbook.createFont();
		titolFont.setFontName("Arial");
		titolFont.setFontHeightInPoints((short)14);
		titolFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		titolStyle.setFont(titolFont);
		
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		capsaleraStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraStyle.setFont(capsaleraFont);
		
		HSSFCellStyle contingutTextStyle = workbook.createCellStyle();
		contingutTextStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		contingutTextStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		HSSFFont contingutFont = workbook.createFont();
		contingutFont.setFontName("Arial");
		contingutFont.setFontHeightInPoints((short)10);
		contingutTextStyle.setFont(contingutFont);	
		
		// Capçaleres
		HSSFRow capsalera0 = sheet.createRow(filaInicial + 1);
		HSSFRow capsalera1 = sheet.createRow(filaInicial + 2);

		// Capçalera de títol
		HSSFCell capsaleraCell = capsalera0.createCell(columnaInicial + 2);
		capsaleraCell.setCellStyle(titolStyle);
		capsaleraCell.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.capsalera.superauditor.titol"));
		
		// Capçaleres de columnes
		HSSFCell numPeticioCellColumnName = capsalera1.createCell(columnaInicial + 2);
		numPeticioCellColumnName.setCellStyle(capsaleraStyle);
		numPeticioCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.numero_peticio"));
		HSSFCell dataCellColumnName = capsalera1.createCell(columnaInicial + 3);
		dataCellColumnName.setCellStyle(capsaleraStyle);
		dataCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.data"));
		HSSFCell procedimentCellColumnName = capsalera1.createCell(columnaInicial + 4);
		procedimentCellColumnName.setCellStyle(capsaleraStyle);
		procedimentCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.usuari"));
		HSSFCell serveiCellColumnName = capsalera1.createCell(columnaInicial + 5);
		serveiCellColumnName.setCellStyle(capsaleraStyle);
		serveiCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.funcionari"));
		HSSFCell titularNomCellColumnName = capsalera1.createCell(columnaInicial + 6);
		titularNomCellColumnName.setCellStyle(capsaleraStyle);
		titularNomCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.procediment"));
		HSSFCell titularDocumentCellColumnName = capsalera1.createCell(columnaInicial + 7);
		titularDocumentCellColumnName.setCellStyle(capsaleraStyle);
		titularDocumentCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.servei"));
		HSSFCell estatCellColumnName = capsalera1.createCell(columnaInicial + 8);
		estatCellColumnName.setCellStyle(capsaleraStyle);
		estatCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.estat"));		
		
		int rowIndex = 2;
		// Contigut de la taula
		if (consultes != null) {
		
			for (ConsultaDto consulta: consultes) {				
				// Mostra la fila d'estadístiques
				HSSFRow filaActual = sheet.createRow(filaInicial + 1 + rowIndex);
				
				HSSFCell numPeticioCell = filaActual.createCell(columnaInicial + 2);
				numPeticioCell.setCellValue(consulta.getScspPeticionId());
				numPeticioCell.setCellStyle(contingutTextStyle);
				
				HSSFCell dataCell = filaActual.createCell(columnaInicial + 3);
				dataCell.setCellValue(consulta.getCreacioData());
				dataCell.setCellStyle(contingutTextStyle);
				
				HSSFCell usuariCell = filaActual.createCell(columnaInicial + 4);
				usuariCell.setCellValue(consulta.getCreacioUsuari().getNomCodi());
				usuariCell.setCellStyle(contingutTextStyle);
				
				HSSFCell funcionariCell = filaActual.createCell(columnaInicial + 5);
				funcionariCell.setCellValue(consulta.getFuncionariNomAmbDocument());
				funcionariCell.setCellStyle(contingutTextStyle);				
				
				HSSFCell procedimentCell = filaActual.createCell(columnaInicial + 6);
				procedimentCell.setCellValue(consulta.getProcedimentCodiNom());
				procedimentCell.setCellStyle(contingutTextStyle);
				
				HSSFCell serveiCell = filaActual.createCell(columnaInicial + 7);
				serveiCell.setCellValue(consulta.getServeiCodiNom());
				serveiCell.setCellStyle(contingutTextStyle);				
				
				HSSFCell estatCell = filaActual.createCell(columnaInicial + 8);
				estatCell.setCellValue(consulta.getEstat());
				estatCell.setCellStyle(contingutTextStyle);
				
				rowIndex++;				
			}
		}
		return filaInicial + 1 + rowIndex;
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
