package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

@Setter
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
		capsaleraEntitatStyle.setAlignment(HorizontalAlignment.LEFT);
		capsaleraEntitatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		HSSFCellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HorizontalAlignment.CENTER);
		capsaleraStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		HSSFFont capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBold(true);
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
