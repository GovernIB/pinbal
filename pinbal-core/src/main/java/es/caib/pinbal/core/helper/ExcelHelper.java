package es.caib.pinbal.core.helper;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExcelHelper implements MessageSourceAware {

	private MessageSource messageSource;
	
	
	
	public byte [] generarReportEstatExcel (List<InformeGeneralEstatDto> informeDades) {
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		HSSFSheet sheet = workbook.createSheet(
				getMessage("informe.general.estat.excel.fulla.titol"));

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
				getMessage("informe.general.estat.excel.columna.entitat.nom"));
		HSSFCell capCell = titolsColumna.createCell(columnaInicial + 1);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.entitat.cif"));
		capCell = titolsColumna.createCell(columnaInicial + 2);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.departament"));
		capCell = titolsColumna.createCell(columnaInicial + 3);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.procediment.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 4);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.procediment.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 5);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.servei.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 6);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.servei.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 7);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.servei.emissor"));
		capCell = titolsColumna.createCell(columnaInicial + 8);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.servei.num.usuaris"));
		capCell = titolsColumna.createCell(columnaInicial + 9);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.peticions.correctes"));
		capCell = titolsColumna.createCell(columnaInicial + 10);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage("informe.general.estat.excel.columna.peticions.erronees"));
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
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			return baos.toByteArray();
		} catch (Exception e) {
			log.error("No s'ha pogut realitzar la exportació." + e);
		}
		
		return null;
		
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

	private String getMessage(String key) {
		String message = messageSource.getMessage(
				key,
				null,
				"???" + key + "???",
				new Locale("ca", "ES"));
		return message;
	}
	
}
