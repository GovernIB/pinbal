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

import es.caib.pinbal.core.dto.InformeGeneralEstatDto;

/**
 * Vista per a generar l'informe de procediments.
 * 
 * @author Josep Gayà
 */
public class InformeGeneralEstatExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;



	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=informeServeis.xls");
		List<InformeGeneralEstatDto> informeDades = (List<InformeGeneralEstatDto>)model.get("informeDades");
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"informe.general.estat.excel.fulla.titol"));

		int filaInicial = 0;
		int columnaInicial = 0;

		HSSFCellStyle capsaleraEntitatStyle = workbook.createCellStyle();
		capsaleraEntitatStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		capsaleraEntitatStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		capsaleraStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		capsaleraStyle.setFont(capsaleraFont);

		HSSFRow titolsColumna = sheet.createRow(filaInicial);
		HSSFCell codiCell = titolsColumna.createCell(columnaInicial);
		codiCell.setCellStyle(capsaleraStyle);
		codiCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.entitat.nom"));
		HSSFCell capCell = titolsColumna.createCell(columnaInicial + 1);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.entitat.cif"));
		capCell = titolsColumna.createCell(columnaInicial + 2);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.departament"));
		capCell = titolsColumna.createCell(columnaInicial + 3);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.procediment.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 4);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.procediment.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 5);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 6);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 7);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.emissor"));
		capCell = titolsColumna.createCell(columnaInicial + 8);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.num.usuaris"));
		capCell = titolsColumna.createCell(columnaInicial + 9);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.peticions.correctes"));
		capCell = titolsColumna.createCell(columnaInicial + 10);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.peticions.erronees"));
		int rowIndex = 1;
		for (InformeGeneralEstatDto informeDada: informeDades) {
			HSSFRow filaDada = sheet.createRow(filaInicial + rowIndex++);
			HSSFCell dadaCell = filaDada.createCell(columnaInicial);
			dadaCell.setCellValue(informeDada.getEntitatNom());
			dadaCell = filaDada.createCell(columnaInicial + 1);
			dadaCell.setCellValue(informeDada.getEntitatCif());
			dadaCell = filaDada.createCell(columnaInicial + 2);
			dadaCell.setCellValue(informeDada.getDepartament());
			dadaCell = filaDada.createCell(columnaInicial + 3);
			dadaCell.setCellValue(informeDada.getProcedimentCodi());
			dadaCell = filaDada.createCell(columnaInicial + 4);
			dadaCell.setCellValue(informeDada.getProcedimentNom());
			dadaCell = filaDada.createCell(columnaInicial + 5);
			dadaCell.setCellValue(informeDada.getServeiCodi());
			dadaCell = filaDada.createCell(columnaInicial + 6);
			dadaCell.setCellValue(informeDada.getServeiNom());
			dadaCell = filaDada.createCell(columnaInicial + 7);
			dadaCell.setCellValue(informeDada.getServeiEmisor().getNom());
			dadaCell = filaDada.createCell(columnaInicial + 8);
			dadaCell.setCellValue(informeDada.getServeiUsuaris());
			dadaCell = filaDada.createCell(columnaInicial + 9);
			dadaCell.setCellValue(informeDada.getPeticionsCorrectes());
			dadaCell = filaDada.createCell(columnaInicial + 10);
			dadaCell.setCellValue(informeDada.getPeticionsErronees());
		}
		autoSize(sheet, 11);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
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
