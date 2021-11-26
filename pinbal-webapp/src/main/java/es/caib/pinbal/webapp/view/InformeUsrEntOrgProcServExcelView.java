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

import es.caib.pinbal.core.dto.InformeProcedimentServeiDto;

public class InformeUsrEntOrgProcServExcelView extends AbstractExcelView implements MessageSourceAware {
	
	private MessageSource messageSource;
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setHeader("Content-Disposition", "Inline; filename=informeUsuarisEntitatOrganProcServei.xls");
		
		List<InformeProcedimentServeiDto> informeDades = (List<InformeProcedimentServeiDto>)model.get("informeDades");
		Boolean isAdministrador = (Boolean)model.get("isAdministrador");
		
		HSSFSheet sheet = workbook.createSheet(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.fulla.titol"));

		int filaInicial = 0;

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
		
		int columnaIndex = 0;
		
		HSSFCell capCell;
		
		if (isAdministrador) {
			capCell = titolsColumna.createCell(columnaIndex);
			capCell.setCellStyle(capsaleraStyle);
			capCell.setCellValue(
					getMessage(
							request,
							"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.entitat.codi"));
			capCell = titolsColumna.createCell(++columnaIndex);
			capCell.setCellStyle(capsaleraStyle);
			capCell.setCellValue(
					getMessage(
							request,
							"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.entitat.nom"));
			capCell = titolsColumna.createCell(++columnaIndex);
			capCell.setCellStyle(capsaleraStyle);
			capCell.setCellValue(
					getMessage(
							request,
							"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.entitat.cif"));
		}
		if (isAdministrador)
			capCell = titolsColumna.createCell(++columnaIndex);
		else 
			capCell = titolsColumna.createCell(columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.organ.codi"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.organ.nom"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.procediment.codi"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.procediment.nom"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.servei.codi"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.servei.nom"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.usuari.codi"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.usuari.nom"));
		capCell = titolsColumna.createCell(++columnaIndex);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.list.informe.usuaris.entitat.organ.procediment.servei.excel.columna.usuari.nif"));

		int rowIndex = 1;
		for (InformeProcedimentServeiDto informeDada: informeDades) {
			columnaIndex = 0;
			HSSFRow filaDada = sheet.createRow(filaInicial + rowIndex++);
			HSSFCell dadaCell;
			if (isAdministrador) {
				dadaCell = filaDada.createCell(columnaIndex);
				dadaCell.setCellValue(informeDada.getEntitatCodi());
				dadaCell = filaDada.createCell(++columnaIndex);
				dadaCell.setCellValue(informeDada.getEntitatNom());
				dadaCell = filaDada.createCell(++columnaIndex);
				dadaCell.setCellValue(informeDada.getEntitatCif());
			}
			if (isAdministrador)
				dadaCell = filaDada.createCell(++columnaIndex);
			else 
				dadaCell = filaDada.createCell(columnaIndex);
			dadaCell.setCellValue(informeDada.getOrganGestorCodi());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getOrganGestorNom());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getProcedimentCodi());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getProcedimentNom());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getServeiCodi());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getServeiNom());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getUsuariCodi());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getUsuariNom());
			dadaCell = filaDada.createCell(++columnaIndex);
			dadaCell.setCellValue(informeDada.getUsuariNif());
		}
		autoSize(sheet, 12);
	}

	@Override
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
