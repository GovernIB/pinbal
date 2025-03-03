/**
 * 
 */
package es.caib.pinbal.webapp.view;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
		List<ConsultaDto> consulta = (List<ConsultaDto>)model.get("consultaList");
		if (consulta != null) {
			taulaConsulta(
					consulta,
					command,					
					request,
					workbook,
					sheet);
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
			HSSFSheet sheet) {
		
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

		HSSFCellStyle contingutDataStyle = workbook.createCellStyle();
		contingutDataStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		contingutDataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		contingutDataStyle.setFont(contingutFont);
		HSSFDataFormat dataFormat = workbook.createDataFormat();
		contingutDataStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy hh:mm:ss"));

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getDefault());

		int indexFila = 0;
		int indexColumna = 0;

		// Capçaleres
		HSSFRow capsalera0 = sheet.createRow(indexFila++);
		HSSFRow capsalera1 = sheet.createRow(indexFila++);

		// Capçalera de títol
		HSSFCell capsaleraCell = capsalera0.createCell(indexColumna);
		capsaleraCell.setCellStyle(titolStyle);
		capsaleraCell.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.capsalera.superauditor.titol"));
		
		// Capçaleres de columnes
		HSSFCell numPeticioCellColumnName = capsalera1.createCell(indexColumna++);
		numPeticioCellColumnName.setCellStyle(capsaleraStyle);
		numPeticioCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.numero_peticio"));
		HSSFCell dataCellColumnName = capsalera1.createCell(indexColumna++);
		dataCellColumnName.setCellStyle(capsaleraStyle);
		dataCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.data"));
		HSSFCell procedimentCellColumnName = capsalera1.createCell(indexColumna++);
		procedimentCellColumnName.setCellStyle(capsaleraStyle);
		procedimentCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.usuari"));
		HSSFCell serveiCellColumnName = capsalera1.createCell(indexColumna++);
		serveiCellColumnName.setCellStyle(capsaleraStyle);
		serveiCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.funcionari"));
		HSSFCell titularNomCellColumnName = capsalera1.createCell(indexColumna++);
		titularNomCellColumnName.setCellStyle(capsaleraStyle);
		titularNomCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.procediment"));
		HSSFCell titularDocumentCellColumnName = capsalera1.createCell(indexColumna++);
		titularDocumentCellColumnName.setCellStyle(capsaleraStyle);
		titularDocumentCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.servei"));
		HSSFCell estatCellColumnName = capsalera1.createCell(indexColumna);
		estatCellColumnName.setCellStyle(capsaleraStyle);
		estatCellColumnName.setCellValue(
				getMessage(
						request,
						"consulta.list.taula.estat"));		
		
		// Contigut de la taula
		if (consultes != null) {
		
			for (ConsultaDto consulta: consultes) {				
				// Mostra la fila d'estadístiques
				HSSFRow filaActual = sheet.createRow(indexFila++);
				indexColumna = 0;
				
				HSSFCell numPeticioCell = filaActual.createCell(indexColumna++);
				numPeticioCell.setCellValue(consulta.getScspPeticionId());
				numPeticioCell.setCellStyle(contingutTextStyle);
				
				HSSFCell dataCell = filaActual.createCell(indexColumna++);
				dataCell.setCellValue(sdf.format(consulta.getCreacioData()));
				dataCell.setCellStyle(contingutDataStyle);
				
				HSSFCell usuariCell = filaActual.createCell(indexColumna++);
				usuariCell.setCellValue(consulta.getCreacioUsuari().getNomCodi());
				usuariCell.setCellStyle(contingutTextStyle);
				
				HSSFCell funcionariCell = filaActual.createCell(indexColumna++);
				funcionariCell.setCellValue(consulta.getFuncionariNomAmbDocument());
				funcionariCell.setCellStyle(contingutTextStyle);
				
				HSSFCell procedimentCell = filaActual.createCell(indexColumna++);
				procedimentCell.setCellValue(consulta.getProcedimentCodiNom());
				procedimentCell.setCellStyle(contingutTextStyle);
				
				HSSFCell serveiCell = filaActual.createCell(indexColumna++);
				serveiCell.setCellValue(consulta.getServeiCodiNom());
				serveiCell.setCellStyle(contingutTextStyle);
				
				HSSFCell estatCell = filaActual.createCell(indexColumna);
				estatCell.setCellValue(consulta.getEstat());
				estatCell.setCellStyle(contingutTextStyle);
				
			}
		}
		return indexFila;
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
