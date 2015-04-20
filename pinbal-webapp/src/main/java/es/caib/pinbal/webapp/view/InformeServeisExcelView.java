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

import es.caib.pinbal.core.dto.ServeiDto;

/**
 * Vista per a generar l'informe de procediments.
 * 
 * @author Josep Gayà
 */
public class InformeServeisExcelView extends AbstractExcelView implements MessageSourceAware {

	private MessageSource messageSource;



	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=informeServeis.xls");
		List<ServeiDto> informeDades = (List<ServeiDto>)model.get("informeDades");
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"informe.serveis.excel.fulla.titol"));

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
						"informe.serveis.excel.columna.codi"));
		HSSFCell nomCell = titolsColumna.createCell(columnaInicial + 1);
		nomCell.setCellStyle(capsaleraStyle);
		nomCell.setCellValue(
				getMessage(
						request,
						"informe.usuaris.excel.columna.nom"));
		int rowIndex = 1;
		for (ServeiDto informeDada: informeDades) {
			HSSFRow filaDada = sheet.createRow(filaInicial + rowIndex++);
			HSSFCell dadaCodiCell = filaDada.createCell(columnaInicial);
			dadaCodiCell.setCellValue(informeDada.getCodi());
			HSSFCell dadaNomCell = filaDada.createCell(columnaInicial + 1);
			dadaNomCell.setCellValue(informeDada.getDescripcio());
		}
		autoSize(sheet, 3);
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
